"""MC2-P1: Market simulator."""

import pandas as pd
import numpy as np
import datetime as dt
import os
from util import get_data, plot_data

def compute_portvals(orders_file = "./orders/orders.csv", start_val = 1000000):
    # this is the function the autograder will call to test your code
    # TODO: Your code here
    orders_df = pd.read_csv(orders_file, index_col='Date', parse_dates=True, na_values=['nan'])
    # print orders_df
    #get the start and end dates from orders
    # start_date = min(orders_df.index.values)
    # end_date = max(orders_df.index.values)
    # start_date = '2006-01-03'
    # end_date = '2009-12-31'
    start_date = '2010-01-04'
    end_date = '2010-12-31'
    dates = pd.date_range(start_date,end_date)
    #make starting bank
    bank = start_val
    # make a dictionary for all the stock allocations
    allocDict = {}
    for i in range(0,len(orders_df)):
        symbol = orders_df.iloc[i]['Symbol']
        allocDict[symbol] = 0
    # get prices for all stock symbols on dates
    syms = allocDict.keys()
    prices_all = get_data(syms, dates)
    prices = prices_all[syms]
    #make your dataframe to put the portvals in
    df = prices_all['SPY']
    #iterate through each date in df
    for i in range(0,len(df)):
        #figure out if date in df is in orders_df
        js = np.where(orders_df.index.values == df.index.values[i])[0]
        if len(js) != 0:
            for j in js:
                # print j
                #add order to allocDict
                symbol = orders_df.iloc[j]['Symbol']
                order = orders_df.iloc[j]['Order']
                shares = orders_df.iloc[j]['Shares']
                # print str(symbol) + str(order) + str(shares)
                if order == 'SELL':
                    shares = shares*(-1)
                #make a temp allocsDict and bank for held back orders
                tempAllocDict = allocDict.copy()
                tempBank = bank
                if symbol in tempAllocDict:
                    tempAllocDict[symbol] = tempAllocDict[symbol] + shares
                else:
                    tempAllocDict[symbol] = shares
                # TODO: update bank value based on order
                tempBank = tempBank - shares*prices.iloc[i][symbol]
                #calculate leverage
                sumStock = 0
                absSumStock = 0
                cash = tempBank
                for key in tempAllocDict:
                    sumStock += tempAllocDict[key]*prices.iloc[i][key]
                    absSumStock += abs(tempAllocDict[key] * prices.iloc[i][key])
                leverage = (float(absSumStock) / float(sumStock + cash))
                # print leverage
                if leverage <= 10000:
                    allocDict = tempAllocDict
                    bank = tempBank
                # print allocDict
                # print bank
        # TODO: print value of portfolio with bank value and number of shares
        stockval = 0
        for key in allocDict:
            stockval += allocDict[key]*prices.iloc[i][key]
        df.iloc[i] = bank + stockval
    #switch over to appropriate nomenclature
    portvals = df

    # In the template, instead of computing the value of the portfolio, we just
    # read in the value of IBM over 6 months
    # start_date = dt.datetime(2008,1,1)
    # end_date = dt.datetime(2008,6,1)
    # portvals = get_data(['IBM'], pd.date_range(start_date, end_date))
    # portvals = portvals[['IBM']]  # remove SPY

    # print 'portvals:'
    # print portvals

    return portvals

def test_code():
    # this is a helper function you can use to test your code
    # note that during autograding his function will not be called.
    # Define input parameters

    # of = "./orders/ordersRuleBased.csv"
    of = "./orders/ordersHold.csv"
    sv = 100000

    # Process orders
    portvals = compute_portvals(orders_file = of, start_val = sv)
    if isinstance(portvals, pd.DataFrame):
        portvals = portvals[portvals.columns[0]] # just get the first column
    else:
        "warning, code did not return a DataFrame"

    # Get portfolio stats
    # Here we just fake the data. you should use your code from previous assignments.
    start_date = dt.datetime(2008,1,1)
    end_date = dt.datetime(2008,6,1)
    cum_ret, avg_daily_ret, std_daily_ret, sharpe_ratio = [0.2,0.01,0.02,1.5]
    cum_ret_SPY, avg_daily_ret_SPY, std_daily_ret_SPY, sharpe_ratio_SPY = [0.2,0.01,0.02,1.5]

    # Compare portfolio against $SPX
    print "Date Range: {} to {}".format(start_date, end_date)
    print
    print "Sharpe Ratio of Fund: {}".format(sharpe_ratio)
    print "Sharpe Ratio of SPY : {}".format(sharpe_ratio_SPY)
    print
    print "Cumulative Return of Fund: {}".format(cum_ret)
    print "Cumulative Return of SPY : {}".format(cum_ret_SPY)
    print
    print "Standard Deviation of Fund: {}".format(std_daily_ret)
    print "Standard Deviation of SPY : {}".format(std_daily_ret_SPY)
    print
    print "Average Daily Return of Fund: {}".format(avg_daily_ret)
    print "Average Daily Return of SPY : {}".format(avg_daily_ret_SPY)
    print
    print "Final Portfolio Value: {}".format(portvals[-1])

    print portvals

if __name__ == "__main__":
    test_code()
