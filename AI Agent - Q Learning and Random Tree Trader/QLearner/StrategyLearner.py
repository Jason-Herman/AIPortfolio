"""
Template for implementing StrategyLearner  (c) 2016 Tucker Balch
"""

import datetime as dt
import QLearner as ql
import pandas as pd
import util as ut
import numpy as np
import time

class StrategyLearner(object):

    # constructor
    def __init__(self, verbose = False):
        self.verbose = verbose

    # this method should create a QLearner, and train it for trading
    def addEvidence(self, symbol = "IBM", \
        sd=dt.datetime(2008,1,1), \
        ed=dt.datetime(2009,1,1), \
        sv = 10000):

        start_time = time.clock()

        self.learner = ql.QLearner(1000,3,.2,.9,.98,.999)
        # add your code to do learning here

        # Get indicators from sd to ed
        lookback = 14
        #Get start date that's 14 days before start date
        indicators_sd = sd - dt.timedelta(days=30)

        # Construct an appropriate DatetimeIndex object.
        dates = pd.date_range(indicators_sd, ed)

        # Read all the relevant price data into a DataFrame.
        price = ut.get_data([symbol], dates)
        price = price.ix[:,[symbol]]

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

        #bring df back to the correct dates
        real_dates = pd.date_range(sd,ed)
        temp_price = ut.get_data([symbol], real_dates)
        first_date = temp_price.index.values[0]

        first_date_index = (np.where(price.index==first_date)[0])[0]
        price = price.ix[first_date_index:]
        # print fixed_price

        #discretize state
        smaR = smaR.ix[first_date_index:]
        smaR[symbol] = pd.qcut(smaR[symbol].values,10).codes

        bbp = bbp.ix[first_date_index:]
        bbp[symbol] = pd.qcut(bbp[symbol].values,10).codes

        rsi = rsi.ix[first_date_index:]
        rsi[symbol] = pd.qcut(rsi[symbol].values,10).codes

        mom = mom.ix[first_date_index:]
        mom[symbol] = pd.qcut(mom[symbol].values,10).codes

        # state = (smaR*1000) + (bbp*100.0001) + (rsi*10) + mom*1
        state = (smaR*100.0001) + (bbp*10) + rsi*1

        states = state.astype(int)




        converged = False
        count = 0
        while not converged:

            #setquerystate

            state = states.ix[0,symbol]

            action = self.learner.querysetstate(state)

            holding = 1

            num_days = states.shape[0]

            total_reward = 0

            for day in range(1,num_days):

                #implement action
                holding = action

                if holding == 1:
                    reward = 0
                elif holding == 2:
                    #get daily return percetage
                    reward = price.ix[day,symbol]/price.ix[day-1,symbol] - 1
                elif holding == 0:
                    #get daily return percetage
                    reward = -1 * (price.ix[day,symbol]/price.ix[day-1,symbol] - 1)
                total_reward +=reward

                state = states.ix[day,symbol]

                action = self.learner.query(state,reward)
                # print state
                # print action



            # print total_reward

            count += 1
            total_time = time.clock() - start_time
            # print total_time
            if total_time > 20:
                converged = True

    # this method should use the existing policy and test it against new data
    def testPolicy(self, symbol = "IBM", \
        sd=dt.datetime(2009,1,1), \
        ed=dt.datetime(2010,1,1), \
        sv = 10000):

        # add your code to do learning here

        # Get indicators from sd to ed
        lookback = 14
        #Get start date that's 14 days before start date
        indicators_sd = sd - dt.timedelta(days=30)

        # Construct an appropriate DatetimeIndex object.
        dates = pd.date_range(indicators_sd, ed)

        # Read all the relevant price data into a DataFrame.
        price = ut.get_data([symbol], dates)
        price = price.ix[:,[symbol]]

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

        #bring df back to the correct dates
        real_dates = pd.date_range(sd,ed)
        temp_price = ut.get_data([symbol], real_dates)
        first_date = temp_price.index.values[0]

        first_date_index = (np.where(price.index==first_date)[0])[0]
        price = price.ix[first_date_index:]
        # print fixed_price

        #discretize state
        smaR = smaR.ix[first_date_index:]
        smaR[symbol] = pd.qcut(smaR[symbol].values,10).codes

        bbp = bbp.ix[first_date_index:]
        bbp[symbol] = pd.qcut(bbp[symbol].values,10).codes

        rsi = rsi.ix[first_date_index:]
        rsi[symbol] = pd.qcut(rsi[symbol].values,10).codes

        mom = mom.ix[first_date_index:]
        mom[symbol] = pd.qcut(mom[symbol].values,10).codes

        # state = (smaR*1000) + (bbp*100.0001) + (rsi*10) + mom*1
        state = (smaR*100.0001) + (bbp*10) + rsi*1

        states = state.astype(int)


        action_df = mom.copy()

        #setquerystate

        state = states.ix[0,symbol]

        action = self.learner.querysetstate(state)

        # print state
        # print action

        holding = 1

        num_days = states.shape[0]

        total_reward = 0

        day = 0

        #add action to dataframe
        if action == 2:
            if holding == 2:
                action_df.ix[day,symbol] = 0.0
            elif holding == 1:
                action_df.ix[day,symbol] = 500.0
            elif holding == 0:
                action_df.ix[day,symbol] = 1000.0
        elif action == 1:
            if holding == 2:
                action_df.ix[day,symbol] = -500.0
            elif holding == 1:
                action_df.ix[day,symbol] = 0.0
            elif holding == 0:
                action_df.ix[day,symbol] = 500.0
        elif action == 0:
            if holding == 2:
                action_df.ix[day,symbol] = -1000.0
            elif holding == 1:
                action_df.ix[day,symbol] = -500.0
            elif holding == 0:
                action_df.ix[day,symbol] = 0

        for day in range(1,num_days):

            #implement action
            holding = action

            if holding == 1:
                reward = 0
            elif holding == 2:
                #get daily return percetage
                reward = price.ix[day,symbol]/price.ix[day-1,symbol] - 1
            elif holding == 0:
                #get daily return percetage
                reward = -1 * (price.ix[day,symbol]/price.ix[day-1,symbol] - 1)
            total_reward +=reward

            state = states.ix[day,symbol]

            action = self.learner.querysetstate(state)

            #add action to dataframe
            if action == 2:
                if holding == 2:
                    action_df.ix[day,symbol] = 0.0
                elif holding == 1:
                    action_df.ix[day,symbol] = 500.0
                elif holding == 0:
                    action_df.ix[day,symbol] = 1000.0
            elif action == 1:
                if holding == 2:
                    action_df.ix[day,symbol] = -500.0
                elif holding == 1:
                    action_df.ix[day,symbol] = 0.0
                elif holding == 0:
                    action_df.ix[day,symbol] = 500.0
            elif action == 0:
                if holding == 2:
                    action_df.ix[day,symbol] = -1000.0
                elif holding == 1:
                    action_df.ix[day,symbol] = -500.0
                elif holding == 0:
                    action_df.ix[day,symbol] = 0

            # print state
            # print action

        # print total_reward

        # print action_df

        return action_df

        # here we build a fake set of trades
        # your code should return the same sort of data
        # dates = pd.date_range(sd, ed)
        # prices_all = ut.get_data([symbol], dates)  # automatically adds SPY
        # trades = prices_all[[symbol,]]  # only portfolio symbols
        # trades_SPY = prices_all['SPY']  # only SPY, for comparison later
        # trades.values[:,:] = 0 # set them all to nothing
        # trades.values[3,:] = 500 # add a BUY at the 4th date
        # trades.values[5,:] = -500 # add a SELL at the 6th date
        # trades.values[6,:] = -500 # add a SELL at the 7th date
        # trades.values[8,:] = 1000 # add a BUY at the 9th date
        # if self.verbose: print type(trades) # it better be a DataFrame!
        # if self.verbose: print trades
        # if self.verbose: print prices_all
        # print trades
        # return trades

if __name__=="__main__":
    print "One does not simply think up a strategy"
