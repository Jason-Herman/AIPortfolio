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

start_date = dt.datetime(2006, 01, 01)
end_date = dt.datetime(2009, 12, 31)
# start_date = dt.datetime(2010, 01, 01)
# end_date = dt.datetime(2010, 12, 31)
symbols = ['IBM']

lookback = 14

indicatorsDict = indicators.get_indicators(symbols, start_date, end_date, lookback)
smaR = indicatorsDict['smaR']
price = indicatorsDict['price']
sma = indicatorsDict['sma']
bbp = indicatorsDict['bbp']
rolling_std = indicatorsDict['rolling_std']
top_band = indicatorsDict['top_band']
bottom_band = indicatorsDict['bottom_band']
rsi = indicatorsDict['rsi']
daily_rets = indicatorsDict['daily_rets']
up_gain = indicatorsDict['up_gain']
down_loss = indicatorsDict['down_loss']
rs = indicatorsDict['rs']
mom = indicatorsDict['mom']

### Use the four indicators to make some kind of trading decision for each day.

sma = smaR

# Orders starts as a NaN array of the same shape/index as price.
orders = price.copy()
orders.ix[:, :] = np.NaN

# Create a copy of RSI but with the SPY column copied to all columns.
spy_rsi = rsi.copy()
spy_rsi.values[:, :] = spy_rsi.ix[:, ['SPY']]

# Create a binary (0-1) array showing when price is above SMA-14.
sma_cross = pd.DataFrame(0, index=sma.index, columns=sma.columns)
sma_cross[sma >= 1] = 1

# Turn that array into one that only shows the crossings (-1 == cross down, +1 == cross up).
sma_cross[1:] = sma_cross.diff()
sma_cross.ix[0] = 0

# Apply our entry order conditions all at once.  This represents our TARGET SHARES
# at this moment in time, not an actual order.
# orders[(sma < 0.95) & (bbp < 0) & (rsi < 30) & (spy_rsi > 30)] = 1
# orders[(sma > 1.05) & (bbp > 1) & (rsi > 70) & (spy_rsi < 70)] = -1

# 176
orders[(sma < 1.00) & (bbp < 0.2) & (rsi < 30) & (spy_rsi > 25) & (mom < -0.048)] = 1
orders[(sma > 1.00) & (bbp > 0.9) & (rsi > 50) & (spy_rsi < 70) & (mom > 0.052)] = -1

# Apply our exit order conditions all at once.  Again, this represents TARGET SHARES.
orders[(sma_cross != 0)] = 0

# We now have -100, 0, or +100 TARGET SHARES on all days that "we care about".  (i.e. those
# days when our strategy tells us something)  All other days are NaN, meaning "hold whatever
# you have".

# Forward fill NaNs with previous values, then fill remaining NaNs with 0.
orders.ffill(inplace=True)
orders.fillna(0, inplace=True)

# But we can at least drop the SPY column.
del orders['SPY']
symbols.remove('SPY')

#make sure that 1's and -1's are at least 10 long. also, if it goes directly from 1 to -1 or vice-versa, then add a zero

#try a while loop, everytime you find a 1 or -1, you set the next 9 to the same value and move the index counter up to next point.
orders10 = orders.copy()
max_count = orders.shape[0]
count = 1
while count < max_count:
    currentVal = orders10.ix[count, "IBM"]
    previousVal = orders10.ix[count-1, "IBM"]
    if currentVal == 1:
        if previousVal == 1:
            count += 1
        elif previousVal == -1:
            orders10.ix[count, "IBM"] = 0
            count += 1
        elif previousVal == 0:
            orders10.ix[count:count+10, "IBM"] = 1
            count +=10
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
    for sym in symbols:
        if orders.ix[day, sym] > 0:
            order_list.append([day.date(), sym, 'BUY', 500])
        elif orders.ix[day, sym] < 0:
            order_list.append([day.date(), sym, 'SELL', 500])

# Dump the orders to stdout.  (Redirect to a file if you wish.)
for order in order_list:
    print "	".join(str(x) for x in order)

# Write to csv
with open("./orders/ordersRuleBased.csv", "wb") as f:
    writer = csv.writer(f)
    writer.writerows(order_list)
with open("./orders/ordersRuleBased.csv", "wb") as f:
    writer = csv.writer(f)
    writer.writerows(order_list)

###BELOW IS RESERVED FOR PLOTTING
#
# # Make plot
# port_vals = marketsim.compute_portvals(orders_file="./orders/ordersRuleBased.csv", start_val=100000)
# hold_vals = marketsim.compute_portvals(orders_file="./orders/ordersHold.csv", start_val=100000)
# port_vals = port_vals/port_vals.ix[0]
# hold_vals = hold_vals / hold_vals.ix[0]
#
# # Make comparison plot
# # port_vals_ML = marketsim.compute_portvals(orders_file="./orders/ordersMLBased.csv", start_val=100000)
# # port_vals_ML = port_vals_ML / port_vals_ML.ix[0]
# #
# # df_temp = pd.concat([hold_vals, port_vals, port_vals_ML], keys=['Benchmark', 'Rule Based Portfolio', 'ML Based Portfolio'], axis=1)
# # ax = df_temp.plot(title='Out of Sample testing', color = ['k','b','g'])
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
# print 'Rule Based Stats'
# print "Sharpe Ratio:", sr
# print "Volatility (stdev of daily returns):", sddr
# print "Average Daily Return:", adr
# print "Cumulative Return:", cr
#
# cr = hold_vals.iloc[-1] - hold_vals.iloc[0]
# # get daily returns df
# dr = hold_vals.pct_change(1).iloc[1:]
# adr = dr.mean()
# sddr = dr.std()
# # get sr
# er = dr - 0
# sr = er.mean() / er.std() * math.sqrt(252)
# print 'Benchmark Stats'
# print "Sharpe Ratio:", sr
# print "Volatility (stdev of daily returns):", sddr
# print "Average Daily Return:", adr
# print "Cumulative Return:", cr
#
#
#
# df_temp = pd.concat([hold_vals,port_vals], keys=['Benchmark','Rule Based Portfolio'], axis=1)
# ax = df_temp.plot(title='Rule Based Performance', color = ['k','b'])
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
