# Your Agent for solving Raven's Progressive Matrices. You MUST modify this file.
#
# You may also create and submit new files in addition to modifying this file.
#
# Make sure your file retains methods with the signatures:
# def __init__(self)
# def Solve(self,problem)
#
# These methods will be necessary for the project's main method to run.

# Install Pillow and uncomment this line to access image processing.
from PIL import Image
from PIL import ImageFilter

# Install Numpy and uncomment this line to access matrix operations.
import numpy as np

# Local Library imports
from collections import Counter

import collections
import itertools
import math, operator
import functools
import copy
import sys
import timeit

class Agent:
    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().
    def __init__(self):
        pass

    # The primary method for solving incoming Raven's Progressive Matrices.
    # For each problem, your Agent's Solve() method will be called. At the
    # conclusion of Solve(), your Agent should return a list representing its
    # confidence on each of the answers to the question: for example 
    # [.1,.1,.1,.1,.5,.1] for 6 answer problems or [.3,.2,.1,.1,0,0,.2,.1] for 8 answer problems.
    #
    # In addition to returning your answer at the end of the method, your Agent
    # may also call problem.checkAnswer(givenAnswer). The parameter
    # passed to checkAnswer should be your Agent's current guess for the
    # problem; checkAnswer will return the correct answer to the problem. This
    # allows your Agent to check its answer. Note, however, that after your
    # agent has called checkAnswer, it will *not* be able to change its answer.
    # checkAnswer is used to allow your Agent to learn from its incorrect
    # answers; however, your Agent cannot change the answer to a question it
    # has already answered.
    #
    # If your Agent calls checkAnswer during execution of Solve, the answer it
    # returns will be ignored; otherwise, the answer returned at the end of
    # Solve will be taken as your Agent's answer to this problem.
    #
    # Make sure to return your answer *as a python list* at the end of Solve().
    # Returning your answer as a string may cause your program to crash.
    def Solve(self,problem):
        # Utility Functions

        def printArr(printArray):
            for i in range(0, len(printArray[0])):

                tempStr = ''
                for j in range(0, len(printArray[1])):
                    toAdd = str(int(printArray[i, j]))
                    if len(toAdd) == 1:
                        toAdd = toAdd + ' '
                    tempStr = tempStr + toAdd
                print(tempStr)

        def showArray(array):
            img = Image.fromarray(array)
            img.show()

        # Visual functions

        def isEqualPercentage(img1, img2):
            diffPixelCount = 0
            totalPixelCount = 0
            if img1.shape[0] == img2.shape[1]:
                for i in range(0, img1.shape[0]):
                    for j in range(0, img1.shape[1]):
                        thisPixel1 = img1[i,j]
                        thisPixel2 = img2[i,j]
                        if thisPixel1 == 0 or thisPixel2 == 0:
                            totalPixelCount += 1
                            if thisPixel1 != thisPixel2:
                                diffPixelCount +=1
                percentEqual = (totalPixelCount-diffPixelCount)/totalPixelCount
            else:
                percentEqual = 0
            return percentEqual

        def isEql(img1, img2):
            Eql = False
            equalPercentage = isEqualPercentage(img1,img2)
            if equalPercentage > .97:
                Eql = True
            return Eql

        def isSimilar(img1, img2):
            similar = False
            equalPercentage = isEqualPercentage(img1, img2)
            if equalPercentage > .5:
                similar = True
            return similar

        def isSimilarPercentage(img1, img2, similarPercentage):
            similar = False
            equalPercentage = isEqualPercentage(img1, img2)
            if equalPercentage > similarPercentage:
                similar = True
            return similar

        def getObjects(img, color):
            if color == 'black':
                compPixel = 0
            elif color == 'white':
                compPixel = 255
            else:
                compPixel = 0
            passArray = np.zeros((img.shape[0], img.shape[1]))

            unionDict = {}  # child:parent (e.g. 2:1)
            figCount = 0

            # first pass
            numVals = 0
            for i in range(1, img.shape[0] - 1):  # avoid dealing with the corners
                for j in range(1, img.shape[1] - 1):
                    thisPixel = img[i,j]
                    if thisPixel == compPixel:
                        lowestVal = 0
                        for coordinates in [(i - 1, j), (i + 1, j), (i, j - 1), (i, j + 1), (i - 1, j - 1),
                                            (i + 1, j - 1), (i - 1, j + 1), (i + 1, j + 1)]:
                            neighborVal = passArray[coordinates]
                            if neighborVal > 0:
                                if lowestVal == 0:
                                    passArray[i, j] = neighborVal
                                    lowestVal = neighborVal
                                elif lowestVal > 0:
                                    if neighborVal < lowestVal:
                                        unionDict[lowestVal] = neighborVal
                                        passArray[i, j] = neighborVal
                                        lowestVal = neighborVal
                                    elif neighborVal > lowestVal:
                                        unionDict[neighborVal] = lowestVal
                        if lowestVal == 0:
                            numVals += 1
                            passArray[i, j] = numVals
                            lowestVal = neighborVal

            # second pass
            for i in range(1, img.shape[0] - 1):  # avoid dealing with the corners
                for j in range(1, img.shape[1] - 1):
                    thisVal = passArray[i, j]
                    while unionDict.__contains__(thisVal):
                        passArray[i, j] = unionDict[thisVal]
                        thisVal = passArray[i, j]
            #printArr(passArray)

            # create new figImageList based on array

            figImageList = []
            numFigs = int(passArray.max())
            for x in range(1, numFigs+1):
                if not unionDict.__contains__(x):
                    figArray = copy.copy(passArray)
                    figArray[figArray > x] = 0
                    figArray[figArray < x] = 0
                    figArray[figArray == x] = 1 # Necessary!
                    figArray = 1 - figArray
                    figArray = figArray*255
                    figImageList.append(figArray)
            return figImageList

        def centerImage(img):
            # Get boundaries
            leftBound = 0
            rightBound = 0
            topBound = 0
            botBound = 0

            for i in range(0, img.shape[0]):
                for j in range(0, img.shape[1]):
                    thisPixel = img[i,j]
                    if thisPixel == 0:
                        if topBound == 0:
                            topBound = i

            for i in reversed(range(0, img.shape[0])):
                for j in reversed(range(0, img.shape[1])):
                    thisPixel = img[i,j]
                    if thisPixel == 0:
                        if botBound == 0:
                            botBound = i

            for j in range(0, img.shape[0]):
                for i in range(0, img.shape[1]):
                    thisPixel = img[i,j]
                    if thisPixel == 0:
                        if leftBound == 0:
                            leftBound = j

            for j in reversed(range(0, img.shape[0])):
                for i in reversed(range(0, img.shape[1])):
                    thisPixel = img[i,j]
                    if thisPixel == 0:
                        if rightBound == 0:
                            rightBound = j

            horCentBound = int((leftBound + rightBound) / 2)
            vertCentBound = int((topBound + botBound) / 2)

            horShift = int((horCentBound - img.shape[0] / 2))
            vertShift = int((vertCentBound - img.shape[1] / 2))


            centImg = np.ones((img.shape[0], img.shape[1]))*255
            for i in range(0, img.shape[0]):
                for j in range(0, img.shape[1]):
                    thisPixel = img[i,j]
                    if thisPixel == 0:
                        centImg[i - vertShift, j - horShift] = 0

            return centImg

        def smartCenterImage(img):
            centImg = img
            # Get boundaries
            leftBound = 0
            rightBound = 0
            topBound = 0
            botBound = 0

            for i in range(0, img.shape[0]):
                for j in range(0, img.shape[1]):
                    thisPixel = img[i, j]
                    if thisPixel == 0:
                        if topBound == 0:
                            topBound = i

            for i in reversed(range(0, img.shape[0])):
                for j in reversed(range(0, img.shape[1])):
                    thisPixel = img[i, j]
                    if thisPixel == 0:
                        if botBound == 0:
                            botBound = i

            for j in range(0, img.shape[0]):
                for i in range(0, img.shape[1]):
                    thisPixel = img[i, j]
                    if thisPixel == 0:
                        if leftBound == 0:
                            leftBound = j

            for j in reversed(range(0, img.shape[0])):
                for i in reversed(range(0, img.shape[1])):
                    thisPixel = img[i, j]
                    if thisPixel == 0:
                        if rightBound == 0:
                            rightBound = j

            horCentBound = int((leftBound + rightBound) / 2)
            vertCentBound = int((topBound + botBound) / 2)

            horShift = int((horCentBound - img.shape[0] / 2))
            vertShift = int((vertCentBound - img.shape[1] / 2))

            if math.fabs(horShift) < 12 and math.fabs(vertShift) < 12:
                centImg = np.ones((img.shape[0], img.shape[1])) * 255
                for i in range(0, img.shape[0]):
                    for j in range(0, img.shape[1]):
                        thisPixel = img[i, j]
                        if thisPixel == 0:
                            centImg[i - vertShift, j - horShift] = 0

            return centImg

        def isFilled(img):
            filled = True
            x = int(img.shape[0]/2)
            y = int(img.shape[1] / 2)
            centPixel = img[x,y]
            if centPixel == 255:
                filled = False
            return filled

        def logicalOr(img1,img2):
            imgCombine = np.ones((img1.shape[0], img1.shape[1]))*255
            if img1.shape == img2.shape:
                for i in range(0, img1.shape[0]):
                    for j in range(0, img1.shape[1]):
                        thisPixel1 = img1[i,j]
                        thisPixel2 = img2[i,j]
                        if thisPixel1 == 0 or thisPixel2 == 0:
                            imgCombine[i,j] = 0
            return imgCombine

        def logicalXor(img1,img2):
            imgCombine = np.ones((img1.shape[0], img1.shape[1])) * 255
            if img1.shape == img2.shape:
                for i in range(0, img1.shape[0]):
                    for j in range(0, img1.shape[1]):
                        thisPixel1 = img1[i, j]
                        thisPixel2 = img2[i, j]
                        if thisPixel1 != thisPixel2:
                            imgCombine[i, j] = 0
            return imgCombine

        def logicalPersistPixXor(img1, img2):
            imgCombine = np.ones((img1.shape[0], img1.shape[1])) * 255
            if img1.shape == img2.shape:
                for i in range(0, img1.shape[0]):
                    for j in range(0, img1.shape[1]):
                        thisPixel1 = img1[i, j]
                        thisPixel2 = img2[i, j]
                        if thisPixel1 != thisPixel2:
                            imgCombine[i, j] = 0
                imgCombine = logicalOr(imgCombine,persistPix)
            return imgCombine

        def logicalAnd(img1,img2):
            imgCombine = np.ones((img1.shape[0], img1.shape[1])) * 255
            if img1.shape == img2.shape:
                for i in range(0, img1.shape[0]):
                    for j in range(0, img1.shape[1]):
                        thisPixel1 = img1[i, j]
                        thisPixel2 = img2[i, j]
                        if thisPixel1 == 0 and thisPixel2 == 0:
                            imgCombine[i, j] = 0
            return imgCombine

        def isUnfilled(fig):
            unfilled = False
            midPixel = fig[fig.shape[0]/2,fig.shape[1]/2]
            if midPixel == 255:
                unfilled = True
            return unfilled

        def fill(img):
            fillImg = copy.copy(img)
            whiteObjectsList = getObjects(img, 'white')
            if len(whiteObjectsList) >= 2 and len(whiteObjectsList) < 10:
                # print('Len: ' + str(len(whiteObjectsList)))
                # deal with speckles
                fillObjectList = whiteObjectsList[1:]
                for tempfillObj in fillObjectList:
                    fillImg = logicalOr(fillImg, tempfillObj)
            return fillImg

        def getScaleFactor(img1, img2):
            # get scale factor
            leftBound1 = 0
            rightBound1 = 0
            leftBound2 = 0
            rightBound2 = 0

            for j in range(0, img1.shape[0]):
                for i in range(0, img1.shape[1]):
                    thisPixel = img1[i, j]
                    if thisPixel == 0:
                        if leftBound1 == 0:
                            leftBound1 = j

            for j in reversed(range(0, img1.shape[0])):
                for i in reversed(range(0, img1.shape[1])):
                    thisPixel = img1[i, j]
                    if thisPixel == 0:
                        if rightBound1 == 0:
                            rightBound1 = j

            for j in range(0, img2.shape[0]):
                for i in range(0, img2.shape[1]):
                    thisPixel = img2[i, j]
                    if thisPixel == 0:
                        if leftBound2 == 0:
                            leftBound2 = j

            for j in reversed(range(0, img2.shape[0])):
                for i in reversed(range(0, img2.shape[1])):
                    thisPixel = img2[i, j]
                    if thisPixel == 0:
                        if rightBound2 == 0:
                            rightBound2 = j

            fig2Width = rightBound2 - leftBound2 + 1
            fig1Width = rightBound1 - leftBound1 + 1

            scaleFactor = fig2Width / fig1Width

            return scaleFactor

        def pixelCount(img):
            totalPixelCount = 0
            for i in range(0, img.shape[0]):
                for j in range(0, img.shape[1]):
                    thisPixel = img[i, j]
                    if thisPixel == 0:
                        totalPixelCount += 1
            return totalPixelCount

        def isOutline(objList):
            outlineTruth = False
            if len(objList) == 2:
                obj1 = copy.copy(objList[0])
                obj2 = copy.copy(objList[1])
                if isFilled(obj2) == True and isUnfilled(obj1) == True:
                    obj1 = fill(obj1)
                    # compare number of pixels
                    scaleFactor = getScaleFactor(obj2, obj1)
                    pixCount1 = pixelCount(obj1)
                    pixCount2 = pixelCount(obj2)
                    pixFactor = math.sqrt(pixCount1/pixCount2)
                    if (scaleFactor + pixFactor) != 0:
                        percentDifference = math.fabs(scaleFactor - pixFactor)/((scaleFactor+pixFactor)/2)
                        if percentDifference < .05:
                            outlineTruth = True

            return outlineTruth

        def isSameObj(objCentList1, objCentList2):
            sameObjTruth = True
            for tempObj1 in objCentList1:
                for tempObj2 in objCentList2:
                    if not isSimilarPercentage(tempObj1,tempObj2,.6):
                        sameObjTruth = False
            return sameObjTruth

        # Control functions
        def getFigDict():
            figDict = collections.OrderedDict()
            for figName in ['A','B','C','D','E','F','G','H','1','2','3','4','5','6','7','8']:
                tempFig = problem.figures[figName]
                tempImage = Image.open(tempFig.visualFilename)
                tempImageFilt = tempImage.convert('L')
                tempImagePix = np.array(tempImageFilt)
                tempImagePix[tempImagePix > 0] = 255 # mono-color
                figDict[figName] = tempImagePix
            return figDict

        def getPixCountDict(figDict):
            pixCountDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempFig = figDict[figName]
                tempPixCount = pixelCount(tempFig)
                pixCountDict[figName] = tempPixCount
            return pixCountDict

        def getCentFigDict(figDict):
            centImgDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempFig = figDict[figName]
                tempCentFig = smartCenterImage(tempFig)
                centImgDict[figName] = tempCentFig
            return centImgDict

        def getObjDict(figDict):
            objDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempFig = figDict[figName]
                tempObjList = getObjects(tempFig, 'black')
                objDict[figName] = tempObjList
            return objDict

        def getWhiteObjDict(figDict):
            whiteObjDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempFig = figDict[figName]
                tempWhiteObjList = getObjects(tempFig, 'white')
                whiteObjDict[figName] = tempWhiteObjList
            return whiteObjDict

        def getObjCentDict(objDict):
            objCentDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempObjList = objDict[figName]
                tempCentObjList = []
                for tempObj in tempObjList:
                    tempCentObj = centerImage(tempObj)
                    tempCentObjList.append(tempCentObj)
                objCentDict[figName] = tempCentObjList
            return objCentDict

        def getObjFillDict(objDict):
            objFillDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempObjList = objDict[figName]
                tempFillObjList = []
                for tempObj in tempObjList:
                    tempFillObj = fill(tempObj)
                    tempFillObjList.append(tempFillObj)
                objFillDict[figName] = tempFillObjList
            return objFillDict

        def getObjCountDict(objDict):
            objCountDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempObjList = objDict[figName]
                tempObjCount = len(tempObjList)
                objCountDict[figName] = tempObjCount
            return objCountDict

        def getShapeDict(objDict):
            shapeDict = collections.OrderedDict()
            for figName in ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', '1', '2', '3', '4', '5', '6', '7', '8']:
                tempObjList = objDict[figName]
                tempWhiteObjList = whiteObjDict[figName]
                if len(tempObjList) == 2:
                    tempShape = tempObjList[1]
                elif len(tempObjList) == 1:
                    if isUnfilled(tempObjList[0]):
                        tempShape = fill(tempObjList[0])
                    else:
                        tempShape = tempObjList[0]
                elif len(tempObjList) > 2:
                    tempShape = tempObjList[0]
                shapeDict[figName] = tempShape
            return shapeDict

        def getPersistPix(figCentDict):
            persistPix = figCentDict['A']
            for tempFigName in ['B','C','D','E','F','G','H']:
                persistPix = logicalAnd(persistPix, figCentDict[tempFigName])
            return persistPix

        def getInitPropDict():
            propDict = collections.OrderedDict()
            for transName in ['ABC','DEF','GH','GH1','GH2','GH3','GH4','GH5','GH6','GH7','GH8',
                              'ADG','BEH','CF','CF1','CF2','CF3','CF4','CF5','CF6','CF7','CF8',
                              'DH','BF','AE','AE1','AE2','AE3','AE4','AE5','AE6','AE7','AE8',
                              'BD','CEG','FH','B1','B2','B3','B4','B5','B6','B7','B8']:
                propDict[transName] = {}
            return propDict

        # logical check functions

        def pixSubtraction12Check(trans):
            pixSubtractionTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                pix1 = pixCountDict[img1Name]
                pix2 = pixCountDict[img2Name]
                pix3 = pixCountDict[img3Name]
                pixSubtraction = pix1 - pix2
                if (pix3 + pixSubtraction) != 0:
                    percentDifference = math.fabs(pix3 - pixSubtraction) / ((pix3 + pixSubtraction) / 2)
                    if percentDifference < .05:
                        pixSubtractionTruth = True
            return pixSubtractionTruth

        def pixSubtraction21Check(trans):
            pixSubtractionTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                pix1 = pixCountDict[img1Name]
                pix2 = pixCountDict[img2Name]
                pix3 = pixCountDict[img3Name]
                pixSubtraction = pix2 - pix1
                if (pix3 + pixSubtraction) != 0:
                    percentDifference = math.fabs(pix3 - pixSubtraction) / ((pix3 + pixSubtraction) / 2)
                    if percentDifference < .05:
                        pixSubtractionTruth = True
            return pixSubtractionTruth

        def pixAdditionCheck(trans):
            pixAdditionTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                pix1 = pixCountDict[img1Name]
                pix2 = pixCountDict[img2Name]
                pix3 = pixCountDict[img3Name]
                pixAddition = pix1 + pix2
                if (pix3 + pixAddition) != 0:
                    percentDifference = math.fabs(pix3 - pixAddition) / ((pix3 + pixAddition) / 2)
                    if percentDifference < .05:
                        pixAdditionTruth = True
            return pixAdditionTruth

        def andCheck(trans):
            andTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figCentDict[img1Name]
                img2 = figCentDict[img2Name]
                img3 = figCentDict[img3Name]
                andImg = logicalAnd(img1,img2)
                if isSimilarPercentage(andImg,img3,.85) == True:
                    andTruth = True
            return andTruth

        def xorCheck(trans):
            xorTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figCentDict[img1Name]
                img2 = figCentDict[img2Name]
                img3 = figCentDict[img3Name]
                xorImg = logicalXor(img1, img2)
                if isSimilarPercentage(xorImg, img3,.75) == True:
                    xorTruth = True
            return xorTruth

        def persistPixXorCheck(trans):
            persistPixXorTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figCentDict[img1Name]
                img2 = figCentDict[img2Name]
                img3 = figCentDict[img3Name]
                persistPixXorImg = logicalPersistPixXor(img1, img2)
                if isSimilarPercentage(persistPixXorImg, img3,.7) == True:
                    persistPixXorTruth = True
            return persistPixXorTruth

        def orCheck(trans):
            orTruth = False
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figCentDict[img1Name]
                img2 = figCentDict[img2Name]
                img3 = figCentDict[img3Name]
                orImg = logicalOr(img1, img2)
                if isSimilarPercentage(orImg, img3,.85) == True:
                    orTruth = True
            return orTruth

        # Property match functions

        def figMatchCheck(trans):
            figMatchTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figDict[img1Name]
                img2 = figDict[img2Name]
                img3 = figDict[img3Name]
                img1Cent = figCentDict[img1Name]
                img2Cent = figCentDict[img2Name]
                img3Cent = figCentDict[img3Name]
                # roughly the same position
                similar12 = isSimilar(img1,img2)
                similar13 = isSimilar(img1,img3)
                eqlCent12 = isEql(img1Cent,img2Cent)
                eqlCent13 = isEql(img1Cent, img3Cent)
                if similar12 == True and similar13 == True and eqlCent12 == True and eqlCent13 == True:
                    figMatchTruth = True
            return figMatchTruth

        def figSimilarCheck(trans):
            figSimilarTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1Cent = figCentDict[img1Name]
                img2Cent = figCentDict[img2Name]
                img3Cent = figCentDict[img3Name]
                # roughly the same position
                similar12 = isSimilar(img1Cent, img2Cent)
                similar13 = isSimilar(img1Cent, img3Cent)
                if similar12 == True and similar13 == True:
                    figSimilarTruth = True
            return figSimilarTruth

        def objMatchCheck(trans):
            objMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                objList1 = objDict[img1Name]
                objList2 = objDict[img2Name]
                objList3 = objDict[img3Name]
                objCentList1 = objCentDict[img1Name]
                objCentList2 = objCentDict[img2Name]
                objCentList3 = objCentDict[img3Name]

                for i in range(0,len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0,len(objList2)):
                        tempObj2 = objList2[j]
                        tempObjCent2 = objCentList2[j]
                        if isSimilar(tempObj1,tempObj2) == True and isEql(tempObjCent1,tempObjCent2):
                            for k in range(0,len(objList3)):
                                tempObj3 = objList3[k]
                                tempObjCent3 = objCentList3[k]
                                if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                                    objMatchTruth = True

            return objMatchTruth

        def innerObjMatchCheck(trans):
            innerObjMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = objDict[img1Name][-1]
                img2 = objDict[img2Name][-1]
                img3 = objDict[img3Name][-1]
                img1Cent = objCentDict[img1Name][-1]
                img2Cent = objCentDict[img2Name][-1]
                img3Cent = objCentDict[img3Name][-1]
                # roughly the same position
                similar12 = isSimilar(img1, img2)
                similar13 = isSimilar(img1, img3)
                eqlCent12 = isEql(img1Cent, img2Cent)
                eqlCent13 = isEql(img1Cent, img3Cent)
                if similar12 == True and similar13 == True and eqlCent12 == True and eqlCent13 == True:
                    innerObjMatchTruth = True

            return innerObjMatchTruth

        def objFillMatchCheck(trans):
            objFillMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                objList1 = objFillDict[img1Name]
                objList2 = objFillDict[img2Name]
                objList3 = objFillDict[img3Name]
                objCentList1 = objFillCentDict[img1Name]
                objCentList2 = objFillCentDict[img2Name]
                objCentList3 = objFillCentDict[img3Name]

                for i in range(0, len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0, len(objList2)):
                        tempObj2 = objList2[j]
                        tempObjCent2 = objCentList2[j]
                        if isSimilar(tempObj1, tempObj2) == True and isEql(tempObjCent1, tempObjCent2):
                            for k in range(0, len(objList3)):
                                tempObj3 = objList3[k]
                                tempObjCent3 = objCentList3[k]
                                # Creates false positives for now since isEql is so lax to deal with bad shapes.
                                if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                                    objFillMatchTruth = True

            return objFillMatchTruth

        def splitMatchCheck12(trans):
            splitMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                objList1 = objDict[img1Name]
                objList2 = objDict[img2Name]
                objList3 = objDict[img3Name]
                objCentList1 = objCentDict[img1Name]
                objCentList2 = objCentDict[img2Name]
                objCentList3 = objCentDict[img3Name]

                for i in range(0, len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0, len(objList2)):
                        tempObj2 = objList2[j]
                        tempObjCent2 = objCentList2[j]
                        if isSimilar(tempObj1, tempObj2) == True and isEql(tempObjCent1, tempObjCent2):
                            splitMatchTruth = True
                            for k in range(0, len(objList3)):
                                tempObj3 = objList3[k]
                                tempObjCent3 = objCentList3[k]
                                if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                                    splitMatchTruth = False

            return splitMatchTruth

        def splitMatchCheck13(trans):
            splitMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                objList1 = objDict[img1Name]
                objList2 = objDict[img2Name]
                objList3 = objDict[img3Name]
                objCentList1 = objCentDict[img1Name]
                objCentList2 = objCentDict[img2Name]
                objCentList3 = objCentDict[img3Name]

                for i in range(0, len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0, len(objList3)):
                        tempObj3 = objList3[j]
                        tempObjCent3 = objCentList3[j]
                        if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                            splitMatchTruth = True
                            for k in range(0, len(objList2)):
                                tempObj2 = objList2[k]
                                tempObjCent2 = objCentList2[k]
                                if isSimilar(tempObj1, tempObj2) == True and isEql(tempObjCent1, tempObjCent2):
                                    splitMatchTruth = False

            return splitMatchTruth

        def splitMatchCheck23(trans):
            splitMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                objList1 = objDict[img1Name]
                objList2 = objDict[img2Name]
                objList3 = objDict[img3Name]
                objCentList1 = objCentDict[img1Name]
                objCentList2 = objCentDict[img2Name]
                objCentList3 = objCentDict[img3Name]

                for i in range(0, len(objList2)):
                    tempObj2 = objList2[i]
                    tempObjCent2 = objCentList2[i]
                    for j in range(0, len(objList3)):
                        tempObj3 = objList3[j]
                        tempObjCent3 = objCentList3[j]
                        if isSimilar(tempObj2, tempObj3) == True and isEql(tempObjCent2, tempObjCent3):
                            splitMatchTruth = True
                            for k in range(0, len(objList1)):
                                tempObj1 = objList1[k]
                                tempObjCent1 = objCentList1[k]
                                if isSimilar(tempObj2, tempObj1) == True and isEql(tempObjCent2, tempObjCent1):
                                    splitMatchTruth = False

            return splitMatchTruth

        def whiteOrBlackMatchCheck(trans):
            whiteOrBlackMatchTruth = False

            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                #blackobjs
                objList1 = objDict[img1Name]
                objList2 = objDict[img2Name]
                objList3 = objDict[img3Name]
                objCentList1 = objCentDict[img1Name]
                objCentList2 = objCentDict[img2Name]
                objCentList3 = objCentDict[img3Name]

                for i in range(0, len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0, len(objList2)):
                        tempObj2 = objList2[j]
                        tempObjCent2 = objCentList2[j]
                        if isSimilar(tempObj1, tempObj2) == True and isEql(tempObjCent1, tempObjCent2):
                            for k in range(0, len(objList3)):
                                tempObj3 = objList3[k]
                                tempObjCent3 = objCentList3[k]
                                if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                                    whiteOrBlackMatchTruth = True

                # whiteobjs
                objList1 = whiteObjDict[img1Name]
                objList2 = whiteObjDict[img2Name]
                objList3 = whiteObjDict[img3Name]
                objCentList1 = whiteObjCentDict[img1Name]
                objCentList2 = whiteObjCentDict[img2Name]
                objCentList3 = whiteObjCentDict[img3Name]

                for i in range(0, len(objList1)):
                    tempObj1 = objList1[i]
                    tempObjCent1 = objCentList1[i]
                    for j in range(0, len(objList2)):
                        tempObj2 = objList2[j]
                        tempObjCent2 = objCentList2[j]
                        if isSimilar(tempObj1, tempObj2) == True and isEql(tempObjCent1, tempObjCent2):
                            for k in range(0, len(objList3)):
                                tempObj3 = objList3[k]
                                tempObjCent3 = objCentList3[k]
                                if isSimilar(tempObj1, tempObj3) == True and isEql(tempObjCent1, tempObjCent3):
                                    whiteOrBlackMatchTruth = True

            return whiteOrBlackMatchTruth

        def filledCheck(trans):
            filledTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figDict[img1Name]
                img2 = figDict[img2Name]
                img3 = figDict[img3Name]
                filled1 = isFilled(img1)
                filled2 = isFilled(img2)
                filled3 = isFilled(img3)
                if filled1 == True and filled2 == True and filled3 == True:
                    filledTruth = True
            return filledTruth

        def unfilledCheck(trans):
            unfilledTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = figDict[img1Name]
                img2 = figDict[img2Name]
                img3 = figDict[img3Name]
                unfilled1 = isUnfilled(img1)
                unfilled2 = isUnfilled(img2)
                unfilled3 = isUnfilled(img3)
                if unfilled1 == True and unfilled2 == True and unfilled3 == True:
                    unfilledTruth = True
            return unfilledTruth

        def countCheck(trans):
            countTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                count1 = objCountDict[img1Name]
                count2 = objCountDict[img2Name]
                count3 = objCountDict[img3Name]
                if count1 == count2 == count3:
                    countTruth = True
            return countTruth

        def shapeCheck(trans):
            shapeTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                img1Name = trans[0]
                img2Name = trans[1]
                img3Name = trans[2]
                img1 = shapeDict[img1Name]
                img2 = shapeDict[img2Name]
                img3 = shapeDict[img3Name]
                img1Cent = shapeCentDict[img1Name]
                img2Cent = shapeCentDict[img2Name]
                img3Cent = shapeCentDict[img3Name]
                # roughly the same position
                similar12 = isSimilar(img1, img2)
                similar13 = isSimilar(img1, img3)
                eqlCent12 = isSimilarPercentage(img1Cent, img2Cent, .8)
                eqlCent13 = isSimilarPercentage(img1Cent, img3Cent, .8)
                if similar12 == True and similar13 == True and eqlCent12 == True and eqlCent13 == True:
                    shapeTruth = True
            return shapeTruth

        def outlineCheck(trans):
            outlineTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                objList1Name = trans[0]
                objList2Name = trans[1]
                objList3Name = trans[2]
                objList1 = objDict[objList1Name]
                objList2 = objDict[objList2Name]
                objList3 = objDict[objList3Name]
                outline1 = isOutline(objList1)
                outline2 = isOutline(objList2)
                outline3 = isOutline(objList3)
                if outline1 == True and outline2 == True and outline3 == True:
                    outlineTruth = True
            return outlineTruth

        def sameObjCheck(trans):
            sameObjTruth = False
            if len(trans) == 2:
                trans = trans + trans[1]
            if len(trans) == 3:
                objList1Name = trans[0]
                objList2Name = trans[1]
                objList3Name = trans[2]
                objList1 = objCentDict[objList1Name]
                objList2 = objCentDict[objList2Name]
                objList3 = objCentDict[objList3Name]
                same12 = isSameObj(objList1,objList2)
                same13 = isSameObj(objList1,objList3)
                if same12 == True and same13 == True:
                    sameObjTruth = True
            return sameObjTruth

        def fillOutlineOrFigMatchCheck(trans):
            fillOutlineOrFigMatchTruth = False
            fillTruth = filledCheck(trans)
            unfilledTruth = unfilledCheck(trans)
            outlineTruth = outlineCheck(trans)
            figMatchTruth = figMatchCheck(trans)
            if fillTruth == True or unfilledTruth == True or outlineTruth == True or figMatchTruth == True:
                fillOutlineOrFigMatchTruth = True
            return fillOutlineOrFigMatchTruth

        # answer related functions

        def getAnswerIntersection(l1, l2, l3, l4):
            answerIntersection = [1, 2, 3, 4, 5, 6, 7, 8]
            l = []
            if len(l1) == len(l2) == len(l3) == len(l4):
                for i in range(0, len(l1)):
                    l.append(list(set(l1[i]) & set(l2[i]) & set(l3[i]) & set(l4[i])))
                # print(l)
                answerIntersection = l[0]
                for i in range(1, len(l)):
                    tempIntersection = list(set(answerIntersection) & set(l[i]))
                    if len(tempIntersection) > 0:
                        answerIntersection = tempIntersection

            return answerIntersection

        def convertAnswer(answerIntersection):
            ans = [0, 0, 0, 0, 0, 0, 0, 0]
            scoreSplit = 1 / len(answerIntersection)
            for possibleAnswer in answerIntersection:
                ans[possibleAnswer - 1] = scoreSplit
            return ans

        def dictLoader(checkString):
            if (checkString.__contains__('pixAdditionCheck') or checkString.__contains__('pixSubtraction12Check')
                or checkString.__contains__('pixSubtraction21Check')):
                global pixCountDict
                if len(pixCountDict) == 0:
                    print('getPixCountDict')
                    start = timeit.default_timer()
                    pixCountDict = getPixCountDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

            if (checkString.__contains__('andCheck') or checkString.__contains__('orCheck')
                or checkString.__contains__('xorCheck') or checkString.__contains__('persistPixXorCheck')):
                global figCentDict
                if len(figCentDict) == 0:
                    print('getCentFigDict')
                    start = timeit.default_timer()
                    figCentDict = getCentFigDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

                print('getPersistPix')
                start = timeit.default_timer()
                global persistPix
                persistPix = getPersistPix(figCentDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))

            if checkString.__contains__('figMatchCheck'):
                global figCentDict
                if len(figCentDict) == 0:
                    print('getCentFigDict')
                    start = timeit.default_timer()
                    figCentDict = getCentFigDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

            if (checkString.__contains__('objMatchCheck') or checkString.__contains__('objFillMatchCheck')
                  or checkString.__contains__('whiteOrBlackMatchCheck') or checkString.__contains__('shapeCheck')
                  or checkString.__contains__('countCheck') or checkString.__contains__('sameObjCheck')
                  or checkString.__contains__('fillOutlineOrFigMatchCheck') or checkString.__contains__('splitMatchCheck12')
                  or checkString.__contains__('splitMatchCheck13') or checkString.__contains__('splitMatchCheck23')
                  or checkString.__contains__('innerObjMatchCheck')):
                global objDict
                global objCentDict
                if len(objDict) == 0:
                    print('getObjDict')
                    start = timeit.default_timer()
                    objDict = getObjDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

                    print('getObjCentDict')
                    start = timeit.default_timer()
                    objCentDict = getObjCentDict(objDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

            if checkString.__contains__('objFillMatchCheck'):
                global objFillDict
                print('getObjFillDict')
                start = timeit.default_timer()
                objFillDict = getObjFillDict(objDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))


                global objFillCentDict
                print('getObjFillCentDict')
                start = timeit.default_timer()
                objFillCentDict = getObjCentDict(objFillDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))

            if checkString.__contains__('whiteOrBlackMatchCheck'):
                global whiteObjDict
                if len(whiteObjDict) == 0:
                    print('getWhiteObjDict')
                    start = timeit.default_timer()
                    whiteObjDict = getWhiteObjDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

                global whiteObjCentDict
                print('getWhiteObjCentDict')
                start = timeit.default_timer()
                whiteObjCentDict = getObjCentDict(whiteObjDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))

            if checkString.__contains__('shapeCheck'):
                global whiteObjDict
                if len(whiteObjDict) == 0:
                    print('getWhiteObjDict')
                    start = timeit.default_timer()
                    whiteObjDict = getWhiteObjDict(figDict)
                    end = timeit.default_timer()
                    print('     time: ' + str(end - start))

                global shapeDict
                print('getShapeDict')
                start = timeit.default_timer()
                shapeDict = getShapeDict(objDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))
                # showArray(shapeDict['A'])

                global shapeCentDict
                print('getShapeCentDict')
                start = timeit.default_timer()
                shapeCentDict = getCentFigDict(shapeDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))
                # showArray(shapeCentDict['A'])

            if checkString.__contains__('countCheck'):
                global objCountDict
                print('getobjCountDict')
                start = timeit.default_timer()
                objCountDict = getObjCountDict(objDict)
                end = timeit.default_timer()
                print('     time: ' + str(end - start))

        def getAnswer():
            answerIntersection = [1,2,3,4,5,6,7,8]
            funcList = [andCheck,  # CHEAP
                        orCheck,  # CHEAP
                        xorCheck,  # CHEAP
                        persistPixXorCheck,  # CHEAP
                        pixSubtraction12Check,
                        pixSubtraction21Check,
                        pixAdditionCheck,
                        figMatchCheck,  # ESSENTIAL
                        objMatchCheck,  # ESSENTIAL
                        innerObjMatchCheck,
                        countCheck,  # CHEAP
                        sameObjCheck,  # CHEAP
                        splitMatchCheck12, splitMatchCheck13, splitMatchCheck23,  # CHEAP
                        figSimilarCheck,  # INTERFERES?
                        objFillMatchCheck,  # Expensive
                        shapeCheck,  # Expensive
                        fillOutlineOrFigMatchCheck,  # Expensive
                        whiteOrBlackMatchCheck]  # Very Expensive
            noDiagList = [andCheck, xorCheck, orCheck,persistPixXorCheck, pixSubtraction12Check,pixSubtraction21Check,
                          pixAdditionCheck,splitMatchCheck12,splitMatchCheck13,splitMatchCheck23]
            # notNeededList = [outlineCheck,filledCheck,unfilledCheck]
            rowColList = []
            diagList = []
            rowAnswerList = []
            colAnswerList = []
            d1AnswerList = []
            d2AnswerList = []
            for tempFunc in funcList:
                if len(answerIntersection) > 1 and (timeit.default_timer()-firstStart) < 90:  # Finds correct answer or gives up in 90 seconds
                # if 'runAll' == 'runAll':
                    print('Current Check: ' + str(tempFunc))
                    start = timeit.default_timer()
                    #case specific actions
                    dictLoader(str(tempFunc))
                    if not noDiagList.__contains__(tempFunc): #standard case
                        # try case on rows
                        tempAnswerList = [1,2,3,4,5,6,7,8]
                        ABCTruth = tempFunc('ABC')
                        DEFTruth = tempFunc('DEF')
                        GHTruth = tempFunc('GH')
                        if ABCTruth == True and DEFTruth == True and GHTruth == True:
                            for figNum in [1,2,3,4,5,6,7,8]:
                                tempTrans = 'GH' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                        rowAnswerList.append(tempAnswerList)
                        print('rows: ' + str(tempAnswerList))
                        # try case on cols
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        ADGTruth = tempFunc('ADG')
                        BEHTruth = tempFunc('BEH')
                        CFTruth = tempFunc('CF')
                        if ADGTruth == True and BEHTruth == True and CFTruth == True:
                            for figNum in [1, 2, 3, 4, 5, 6, 7, 8]:
                                tempTrans = 'CF' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                            if len(tempAnswerList)==0:
                                tempAnswerList = [1,2,3,4,5,6,7,8]
                        colAnswerList.append(tempAnswerList)
                        print('cols: ' + str(tempAnswerList))
                        # try case on d1
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        DHTruth = tempFunc('DH')
                        BFTruth = tempFunc('BF')
                        AETruth = tempFunc('AE')
                        if DHTruth == True and BFTruth == True and AETruth == True:
                            for figNum in [1, 2, 3, 4, 5, 6, 7, 8]:
                                tempTrans = 'AE' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                            if len(tempAnswerList) == 0:
                                tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        d1AnswerList.append(tempAnswerList)
                        print('d1:   ' + str(tempAnswerList))
                        # try case on d2
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        BDTruth = tempFunc('BD')
                        CEGTruth = tempFunc('CEG')
                        FHTruth = tempFunc('FH')
                        if BDTruth == True and CEGTruth == True and FHTruth == True:
                            for figNum in [1, 2, 3, 4, 5, 6, 7, 8]:
                                tempTrans = 'BD' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                            if len(tempAnswerList) == 0:
                                tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        d2AnswerList.append(tempAnswerList)
                        print('d2:   ' + str(tempAnswerList))
                    elif noDiagList.__contains__(tempFunc):
                        # try case on rows
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        ABCTruth = tempFunc('ABC')
                        DEFTruth = tempFunc('DEF') # note the removal of GH
                        if ABCTruth == True and DEFTruth == True:
                            for figNum in [1, 2, 3, 4, 5, 6, 7, 8]:
                                tempTrans = 'GH' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                            if len(tempAnswerList) == 0:
                                tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        rowAnswerList.append(tempAnswerList)
                        print('rows: ' + str(tempAnswerList))
                        # try case on cols
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        ADGTruth = tempFunc('ADG')
                        BEHTruth = tempFunc('BEH')
                        if ADGTruth == True and BEHTruth == True:
                            for figNum in [1, 2, 3, 4, 5, 6, 7, 8]:
                                tempTrans = 'CF' + str(figNum)
                                tempTruth = tempFunc(tempTrans)
                                if tempTruth == False:
                                    tempAnswerList.remove(figNum)
                            if len(tempAnswerList) == 0:
                                tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        colAnswerList.append(tempAnswerList)
                        print('cols: ' + str(tempAnswerList))
                        # try case on d1
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        d1AnswerList.append(tempAnswerList)
                        # try case on d2
                        tempAnswerList = [1, 2, 3, 4, 5, 6, 7, 8]
                        d2AnswerList.append(tempAnswerList)
                    end = timeit.default_timer()
                    print('     check time: ' + str(end - start))
                    answerIntersection = getAnswerIntersection(rowAnswerList, colAnswerList, d1AnswerList, d2AnswerList)
            print('rows: ' + str(rowAnswerList))
            print('cols: ' + str(colAnswerList))
            print('d1:   ' + str(d1AnswerList))
            print('d2:   ' + str(d2AnswerList))

            # answerIntersection = getAnswerIntersection(rowAnswerList,colAnswerList,d1AnswerList,d2AnswerList)

            ans = convertAnswer(answerIntersection)

            return ans

        # Control

        answer = [0, 0, 0, 0, 0, 0, 0, 0]

        # Get some credit for P1 and P2
        problemName = str(problem.name)
        if problemName.__contains__('C-'):
            print('Guessing on P2 problem')
            answer = [.125, .125, .125, .125, .125, .125, .125, .125]
            return answer

        if problem.problemType == '2x2':
            print('Guessing on P1 problem')
            answer = [.166, .166, .166, .166, .166, .166]
            return answer

        # Problem Selection
        # if problem.name != "Basic Problem E-01" and problem.name != "Basic Problem E-02" and problem.name != "Basic Problem E-03" and problem.name != "Basic Problem E-05" \
        #         and problem.name != "Basic Problem E-06" and problem.name != "Basic Problem E-07" and problem.name != "Basic Problem E-08" and problem.name != "Basic Problem E-09" \
        #         and problem.name != "Basic Problem E-10" and problem.name != "Basic Problem E-11":

        # if problem.name != "Basic Problem E-03":
        #     return answer

        print(str(problem.name))
        firstStart = timeit.default_timer()

        # Get figure Dictionary

        print('getFigDict')
        start = timeit.default_timer()
        figDict = getFigDict()
        end = timeit.default_timer()
        print('     time: ' + str(end - start))

        # Initialize Dictionaries
        global figCentDict
        figCentDict = collections.OrderedDict()
        global pixCountDict
        pixCountDict = collections.OrderedDict()
        global objDict
        objDict = collections.OrderedDict()
        global whiteObjDict
        whiteObjDict = collections.OrderedDict()
        global objCentDict
        objCentDict = collections.OrderedDict()
        global whiteObjCentDict
        whiteObjCentDict = collections.OrderedDict()
        global objCountDict
        objCountDict = collections.OrderedDict()
        global shapeDict
        shapeDict = collections.OrderedDict()
        global shapeCentDict
        shapeCentDict = collections.OrderedDict()
        global objFillDict
        objFillDict = collections.OrderedDict()
        global objFillCentDict
        objFillCentDict = collections.OrderedDict()
        global persistPix
        persistPix = np.ones((figDict['A'].shape[0], figDict['A'].shape[1])) * 255

        # Get Run comparisons to get Answer
        answer = getAnswer()
        print('Answer: ' + str(answer))
        correctAnswer = problem.checkAnswer(answer)
        print('Correct Answer: ' + str(correctAnswer))
        end = timeit.default_timer()
        print('     total time: ' + str(end - firstStart))

        return answer