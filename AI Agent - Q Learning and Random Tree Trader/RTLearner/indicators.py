import numpy as np
import pandas as pd
import datetime as dt
import math
import time
import util
import sys
import matplotlib.pyplot as plt


def get_indicators(symbols, start_date, end_date, lookback):
    print "Constructing SMA, BB%, RSI, and Momentum from " + str(start_date.date()) + \
          " to " + str(end_date.date())

    # Construct an appropriate DatetimeIndex object.
    dates = pd.date_range(start_date, end_date)

    # Read all the relevant price data (plus SPY) into a DataFrame.
    price = util.get_data(symbols, dates)

    # Add SPY to the symbol list for convenience.
    symbols.append('SPY')


    ### Calculate SMA-14 over the entire period in a single step.
    sma = pd.rolling_mean(price,window=lookback, min_periods=lookback)


    ### Calculate Bollinger Bands (14 day) over the entire period.
    rolling_std = pd.rolling_std(price,window=lookback, min_periods=lookback)
    top_band = sma + (2 * rolling_std)
    bottom_band = sma - (2 * rolling_std)

    bbp = (price - bottom_band) / (top_band - bottom_band)


    ### Now we can turn the SMA into an SMA ratio, which is more useful.
    smaR = price / sma

    ### Calculate Momentum (14 day) for the entire date range for all symbols.
    mom = (price / price.shift(lookback - 1)) - 1

    ### Calculate Relative Strength Index (14 day) for the entire date range for all symbols.
    rs = price.copy()
    rsi = price.copy()

    # Calculate daily_rets for the entire period (and all symbols).
    daily_rets = price.copy()
    daily_rets.values[1:, :] = price.values[1:, :] - price.values[:-1, :]
    daily_rets.values[0, :] = np.nan

    # Split daily_rets into a same-indexed DataFrame of only up days and only down days,
    # and accumulate the total-up-days-return and total-down-days-return for every day.
    up_rets = daily_rets[daily_rets >= 0].fillna(0).cumsum()
    down_rets = -1 * daily_rets[daily_rets < 0].fillna(0).cumsum()

    # Apply the sliding lookback window to produce for each day, the cumulative return
    # of all up days within the window, and separately for all down days within the window.
    up_gain = price.copy()
    up_gain.ix[:, :] = 0
    up_gain.values[lookback:, :] = up_rets.values[lookback:, :] - up_rets.values[:-lookback, :]

    down_loss = price.copy()
    down_loss.ix[:, :] = 0
    down_loss.values[lookback:, :] = down_rets.values[lookback:, :] - down_rets.values[:-lookback, :]

    # Now we can calculate the RS and RSI all at once.
    rs = (up_gain / lookback) / (down_loss / lookback)
    rsi = 100 - (100 / (1 + rs))
    rsi.ix[:lookback, :] = np.nan

    # An infinite value here indicates the down_loss for a period was zero (no down days), in which
    # case the RSI should be 100 (its maximum value).
    rsi[rsi == np.inf] = 100

    return {'smaR':smaR, 'price':price, 'sma':sma, 'bbp':bbp, 'rolling_std':rolling_std, 'top_band':top_band, 'bottom_band':bottom_band, 'rsi':rsi, 'daily_rets':daily_rets, 'up_gain':up_gain, 'down_loss':down_loss, 'rs':rs, 'mom':mom}


### Main function.  Not called if imported elsewhere as a module. Creates charts for indicators.
if __name__ == "__main__":
    start_date = dt.datetime(2006, 01, 01)
    end_date = dt.datetime(2009, 12, 31)
    symbols = ['IBM']

    lookback = 14

    indicatorsDict = get_indicators(symbols, start_date, end_date, lookback)
    smaR = indicatorsDict['smaR'].ix[:, ['IBM']]
    price = indicatorsDict['price'].ix[:, ['IBM']]
    sma = indicatorsDict['sma'].ix[:, ['IBM']]
    bbp = indicatorsDict['bbp'].ix[:, ['IBM']]
    rolling_std = indicatorsDict['rolling_std'].ix[:, ['IBM']]
    top_band = indicatorsDict['top_band'].ix[:, ['IBM']]
    bottom_band = indicatorsDict['bottom_band'].ix[:, ['IBM']]
    rsi = indicatorsDict['rsi'].ix[:, ['IBM']]
    daily_rets = indicatorsDict['daily_rets'].ix[:, ['IBM']]
    up_gain = indicatorsDict['up_gain'].ix[:, ['IBM']]
    down_loss = indicatorsDict['down_loss'].ix[:, ['IBM']]
    rs = indicatorsDict['rs'].ix[:, ['IBM']]
    mom = indicatorsDict['mom'].ix[:, ['IBM']]

    spy_rsi = indicatorsDict['rsi'].ix[:, ['SPY']]

    #make normalized charts and try to get price on every chart

    priceN = price / price.iloc[0]
    smaN = sma / sma.iloc[15]
    bottom_bandN = bottom_band / bottom_band.iloc[15]
    top_bandN = top_band / top_band.iloc[15]

#   create charts
    #plot smaR, then price and sma on same chart
    # df_temp = pd.concat([smaR], keys=['smaR'], axis=1)
    # ax = df_temp.plot(title='Price / Simple Moving Average')

    df_temp = pd.concat([smaR,priceN, smaN], keys=['PriceOverSMA','Price', 'SMA'], axis=1)
    ax = df_temp.plot(title='Price / Simple Moving Average')

  # plot bbp, then sma, top_band, bottom_band, then rolling_std on same chart
    df_temp = pd.concat([bbp], keys=['Bollinger Bands'], axis=1)
    ax = df_temp.plot(title='Bollinger Bands')

    # df_temp = pd.concat([rolling_std], keys=['rolling_std'], axis=1)
    # ax = df_temp.plot(title='Volatility (rolling standard deviation)')

    df_temp = pd.concat([price, top_band, bottom_band], keys=['Price', 'Top Band', 'Bottom Band'], axis=1)
    ax = df_temp.plot(title='Top and Bottom Bands')

    # plot rsi, then daily_rets, then up_gain and downloss then rs on same chart
    df_temp = pd.concat([rsi, spy_rsi, price], keys=['IBM RSI', 'SPY RSI', 'IBM PRICE'], axis=1)
    ax = df_temp.plot(title='Relative Strength Index')

    # df_temp = pd.concat([rs], keys=['Relative Strength'], axis=1)
    # ax = df_temp.plot(title='Relative Strength')

    df_temp = pd.concat([up_gain, down_loss], keys=['Up Gain', 'Down Loss'], axis=1)
    ax = df_temp.plot(title='Up Gain and Down Loss')

    # df_temp = pd.concat([daily_rets], keys=['Daily Returns'], axis=1)
    # ax = df_temp.plot(title='Daily Returns')

    # plot mom, then price
    df_temp = pd.concat([mom, priceN], keys=['Momentum','Price'], axis=1)
    ax = df_temp.plot(title='Momentum')

    plt.show()



