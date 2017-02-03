"""
A simple wrapper for random tree regression.  (c) 2016 Jason Herman
"""

import numpy as np
from random import randint


class RTLearner(object):
    def __init__(self, leaf_size=1, verbose=False):
        self.verbose = verbose
        self.leafSize = leaf_size
        self.featNum = 0
        # pass # move along, these aren't the drones you're looking for

    def buildTree(self, X, Y):
        if X.shape[0] <= self.leafSize:  # down to a single row
            return np.array([[-1, np.mean(Y), -1, -1]])  # make sure you are returning correctly
        elif np.all(Y == Y[0]):  # all y are the same
            return np.array([[-1, Y[0], -1, -1]])
        else:
            # add the rest of the logic here
            rowNum = X.shape[0] - 1
            # get a new split val if it doesn't actually split
            badSplitVal = True
            while badSplitVal:
                splitFeat = randint(0, self.featNum)
                splitVal = (X[randint(0, rowNum), splitFeat] + X[randint(0, rowNum), splitFeat]) / 2
                if splitVal != X[:, splitFeat].max() and splitVal != X[:, splitFeat].min():
                    badSplitVal = False
            LT = self.buildTree(X[X[:, splitFeat] <= splitVal], Y[X[:, splitFeat] <= splitVal])
            # print LT
            RT = self.buildTree(X[X[:, splitFeat] > splitVal], Y[X[:, splitFeat] > splitVal])
            # print RT
            root = np.array([[splitFeat, splitVal, 1, LT.shape[0] + 1]])
            # print root
            return np.append(root, np.append(LT, RT, axis=0), axis=0)

    def addEvidence(self, dataX, dataY):
        """
        @summary: Add training data to learner
        @param dataX: X values of data to add
        @param dataY: the Y training values
        """
        # Build tree here (make sure to print while training)

        self.featNum = dataX.shape[1] - 1
        self.tree = self.buildTree(dataX, dataY)


        # # slap on 1s column so linear regression finds a constant term
        # newdataX = np.ones([dataX.shape[0],dataX.shape[1]+1])
        # newdataX[:,0:dataX.shape[1]]=dataX
        #
        # # build and save the model
        # self.model_coefs, residuals, rank, s = np.linalg.lstsq(newdataX, dataY)

    def queryTree(self, point, parseTree):
        factor = int(parseTree[0, 0])
        sv = parseTree[0, 1]
        if factor == -1:
            return sv
        elif point[factor] <= sv:
            lt = int(parseTree[0, 2])
            return self.queryTree(point, parseTree[lt:, :])
        elif point[factor] > sv:
            rt = int(parseTree[0, 3])
            return self.queryTree(point, parseTree[rt:, :])

    def query(self, points):
        """
        @summary: Estimate a set of test points given the model we built.
        @param points: should be a numpy array with each row corresponding to a specific query.
        @returns the estimated values according to the saved model.
        """
        size = points.shape[0]
        predY = np.empty(size)
        for i in range(0, size):
            predY[i] = self.queryTree(points[i], self.tree)
        # print predY
        return predY

        # return (self.model_coefs[:-1] * points).sum(axis = 1) + self.model_coefs[-1]


if __name__ == "__main__":
    print "the secret clue is 'zzyzx'"