import numpy as np
import pandas as pd
import datetime as dt
import math
import time
import util
import sys
import matplotlib.pyplot as plt
import csv

import indicators
import marketsim
import RTLearner as rt
start_date = dt.datetime(2006, 01, 01)
end_date = dt.datetime(2009, 12, 31)
start_date_test = dt.datetime(2006, 01, 01)
end_date_test = dt.datetime(2009, 12, 31)
# start_date_test = dt.datetime(2010, 01, 01)
# end_date_test = dt.datetime(2010, 12, 31)
symbols = ['IBM']

lookback = 14

# Get testX
indicatorsDict = indicators.get_indicators(symbols, start_date_test, end_date_test, lookback)
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

smaTest = indicatorsDict['sma'].ix[:, ['IBM']]

sma = smaR

# Get X DATA

df_temp = pd.concat([sma, bbp, rsi, spy_rsi, mom], keys=['sma', 'bbp', 'rsi', 'spy_rsi', 'mom', ], axis=1)
df_temp = df_temp.ix[14:-11]

testX = df_temp.as_matrix()

# Have testX

indicatorsDict = indicators.get_indicators(symbols, start_date, end_date, lookback)
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

sma = smaR

# Get X DATA

# YBUY = 1.04
# YSELL = .96
YBUY = 1.01
YSELL = .95
df_temp = pd.concat([sma, bbp, rsi, spy_rsi, mom], keys=['sma', 'bbp', 'rsi', 'spy_rsi', 'mom',], axis=1)
df_temp = df_temp.ix[14:-11]

trainX = df_temp.as_matrix()

ret = price.shift(-10) / price
ret = ret[14:-11]
ret = ret.as_matrix()
trainY = np.copy(ret)
trainY[:] = 0
trainY[ret>=YBUY] = 1
trainY[ret<=YSELL] = -1

learner = rt.RTLearner(leaf_size=5, verbose=False)
learner.addEvidence(trainX, trainY)  # train it
predY = learner.query(testX)

#convert predY back to buy and sell.
orders = np.copy(predY)
orders[:] = 0
orders[predY >.5] = 1
orders[predY < -.5] = -1

df_temp = smaTest[14:-11]
df_temp.ix[:, 'IBM'] = orders
orders = df_temp.copy()

# make sure that 1's and -1's are at least 10 long. also, if it goes directly from 1 to -1 or vice-versa, then add a zero

# try a while loop, everytime you find a 1 or -1, you set the next 9 to the same value and move the index counter up to next point.
orders10 = orders.copy()
max_count = orders.shape[0]
count = 1
while count < max_count:
    currentVal = orders10.ix[count, "IBM"]
    previousVal = orders10.ix[count - 1, "IBM"]
    if currentVal == 1:
        if previousVal == 1:
            count += 1
        elif previousVal == -1:
            orders10.ix[count, "IBM"] = 0
            count += 1
        elif previousVal == 0:
            orders10.ix[count:count + 10, "IBM"] = 1
            count += 10
    elif currentVal == -1:
        if previousVal == -1:
            count += 1
        elif previousVal == 1:
            orders10.ix[count, "IBM"] = 0
            count += 1
        elif previousVal == 0:
            orders10.ix[count:count + 10, "IBM"] = -1
            count += 10
    elif currentVal == 0:
        count += 1

orders = orders10.copy()


# We now have a dataframe with our TARGET SHARES on every day, including holding periods.

# Now take the diff, which will give us an order to place only when the target shares changed.
orders[1:] = orders.diff()
orders.ix[0] = 0

# And now we have our orders array, just as we wanted it, with no iteration.



# It would be hard to vectorize our weird formatting output, which triggers on individual
# elements and needs the index values (row and column).



# And more importantly, drop all rows with no non-zero values (i.e. no orders).
orders = orders.loc[(orders != 0).any(axis=1)]

# Now we have only the days that have orders.  That's better, at least!
order_list = []
order_list.append(['Date', 'Symbol', 'Order', 'Shares'])
hold_list = []
hold_list.append(['Date', 'Symbol', 'Order', 'Shares'])
hold_list.append(['2006-01-03', 'IBM', 'BUY', 500])

for day in orders.index:
    for sym in ['IBM']:
        if orders.ix[day, sym] > 0:
            order_list.append([day.date(), sym, 'BUY', 500])
        elif orders.ix[day, sym] < 0:
            order_list.append([day.date(), sym, 'SELL', 500])

# Dump the orders to stdout.  (Redirect to a file if you wish.)
for order in order_list:
    print "	".join(str(x) for x in order)

# Write to csv
with open("./orders/ordersMLBased.csv", "wb") as f:
    writer = csv.writer(f)
    writer.writerows(order_list)
with open("./orders/ordersMLBased.csv", "wb") as f:
    writer = csv.writer(f)
    writer.writerows(order_list)

###BELOW IS RESERVED FOR PLOTTING
#
# # Make plot
# port_vals = marketsim.compute_portvals(orders_file="./orders/ordersMLBased.csv", start_val=100000)
# hold_vals = marketsim.compute_portvals(orders_file="./orders/ordersHold.csv", start_val=100000)
# port_vals = port_vals / port_vals.ix[0]
# hold_vals = hold_vals / hold_vals.ix[0]
#
# # port_valsRules = marketsim.compute_portvals(orders_file="./orders/ordersRuleBased.csv", start_val=100000)
# # port_valsRules = port_valsRules / port_valsRules.ix[0]
#
# # Print factors from port and hold vals
# # Get stats
# cr = port_vals.iloc[-1] - port_vals.iloc[0]
# # get daily returns df
# dr = port_vals.pct_change(1).iloc[1:]
# adr = dr.mean()
# sddr = dr.std()
# # get sr
# er = dr - 0
# sr = er.mean() / er.std() * math.sqrt(252)
# print 'ML Based Stats'
# print "Sharpe Ratio:", sr
# print "Volatility (stdev of daily returns):", sddr
# print "Average Daily Return:", adr
# print "Cumulative Return:", cr
#
# df_temp = pd.concat([hold_vals, port_vals], keys=['Benchmark','ML Based Portfolio'], axis=1)
# ax = df_temp.plot(title='ML Based Performance', color = ['k','g'])
#
# position = 0
# long = []
# short = []
# ext = []
# for order in order_list[1::]:
#     if position == 0:
#         if order[2] == 'BUY':
#             long.append(order[0])
#             position = 1
#         elif order[2] == 'SELL':
#             short.append(order[0])
#             position = -1
#     elif position != 0:
#         ext.append(order[0])
#         position = 0
#
# for xc in long:
#     ax.axvline(x=xc, color='g', linestyle='-')
# for xc in short:
#     ax.axvline(x=xc, color='r', linestyle='-')
# for xc in ext:
#     ax.axvline(x=xc, color='k', linestyle=':')
#
#
# plt.show()
