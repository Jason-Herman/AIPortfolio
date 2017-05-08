# This file is your main submission that will be graded against. Only copy-paste
# code on the relevant classes included here from the IPython notebook. Do not
# add any classes or functions to this file that are not part of the classes
# that we want.
from __future__ import division
import math
from osm2networkx import *
import random
import pickle
import sys
# Comment the next line when submitting to bonnie
# import matplotlib.pyplot as plt

# Implement a heapq backed priority queue (accompanying the relevant question)

import heapq

class PriorityQueue():
    """Implementation of a priority queue
    to store nodes during search."""

    def __init__(self):
        self.queue = []
        self.current = 0

    def next(self):
        if self.current >=len(self.queue):
            self.current
            raise StopIteration

        out = self.queue[self.current]
        self.current += 1

        return out

    def pop(self):
        #PERFNOTE: using heap instead of unsorted list provides O(logN) instead of O(logN)
        return heapq.heappop(self.queue)

    # This is a hint, you might require this in ucs,
    # however, if you choose not to use it, you are free to
    # define your own method and not use it.
    def remove(self, nodeId):
        '''PERFNOTE: This is O(N) - You could you use a blacklist when popping to get down to N time
        http://stackoverflow.com/questions/13800947/deleting-from-python-heapq-in-ologn'''
        #NOTE: pulled logic from SO.
        for i in range(len(self.queue)):
            if self.queue[i][1] == nodeId:
                self.queue[i], self.queue[-1] = self.queue[-1], self.queue[i] #move match to the end
                popped = self.queue.pop() #discard match
                heapq.heapify(self.queue) #heapify again since you broke the order
                break

    def __iter__(self):
        return self

    def __str__(self):
        return 'PQ:[%s]'%(', '.join([str(i) for i in self.queue]))

    def append(self, node):
        # DEVNOTE: pq.append((a,'a')) called from test. Node is (value, 'name')
        heapq.heappush(self.queue, node)

    def __contains__(self, key):
        self.current = 0
        return key in [n for v,n in self.queue]

    def __eq__(self, other):
        return self == other

    def size(self):
        return len(self.queue)
    
    def clear(self):
        self.queue = []
        
    def top(self):
        return self.queue[0]

    __next__ = next

#Warmup exercise: Implement breadth-first-search
def breadth_first_search(graph, start, goal):
    """Run a breadth-first search from start
    to goal and return the path."""
    if start == goal: return []
    frontier = PriorityQueue()
    explored = []
    frontier.append([0,start])
    while frontier:
        path = frontier.pop()
        s = path[-1]
        explored.append(s)
        if s == goal:
            return path[1:] # remove cost
        for a in graph[s]:
            if a not in explored:
                newPath = list(path)
                newPath.append(a)
                #NOTE: just counting hops, not actual distance
                d = 1
                newPath[0] += d
                #NOTE: Check if newPath reaches goal
                if a == goal:
                    return newPath[1:]
                frontier.append(newPath)

#Warmup exercise: Implement uniform_cost_search
def uniform_cost_search(graph, start, goal):
    """Run a UC search from start
    to goal and return the path."""
    if start == goal: return []
    frontier = PriorityQueue()
    explored = []
    frontier.append([0,start])
    while frontier:
        path = frontier.pop()
        s = path[-1]
        explored.append(s)
        if s == goal:
            print 'path: ', path
            return path[1:] # remove cost
        for a in graph[s]:
            if a not in explored:
                newPath = list(path)
                newPath.append(a)
                d = graph[s][a]['weight']
                newPath[0] += d
                frontier.append(newPath)

# Warmup exercise: Implement A*
def null_heuristic(graph, v, goal ):
    return 0

import math
def distance(p0, p1):
    return math.sqrt((p0[0] - p1[0])**2 + (p0[1] - p1[1])**2)

# Warmup exercise: Implement the euclidean distance heuristic
def euclidean_dist_heuristic(graph, v, goal):
    """Return the Euclidean distance from
    node v to the goal."""
    if v == goal:
        return 0
    if graph.node[v].get('position'):
        strPos = 'position'
    else:
        strPos = 'pos'
    a = graph.node[v][strPos]
    b = graph.node[goal][strPos]
    #find the dist from a to b
    h = int(round(distance(a,b)))
    return h

# Warmup exercise: Implement A* algorithm
def a_star(graph, start, goal, heuristic):
    """Run A* search from the start to
    goal using the specified heuristic
    function, and return the final path."""
    if start == goal: return []
    frontier = PriorityQueue()
    explored = []
    g = 0
    h = heuristic(graph, start, goal)
    f = g + h
    frontier.append([f,g,h,start])
    while frontier:
        path = frontier.pop()
        s = path[-1]
        explored.append(s)
        if s == goal:
            print 'path: ', path
            return path[3:] # remove f,g,h
        for a in graph[s]:
            if a not in explored:
                newPath = list(path)
                newPath.append(a)
                #update f,g,h
                h = heuristic(graph, a, goal)
                gAdd = graph[s][a]['weight']
                g = newPath[1] + gAdd
                f = g + h
                newPath[0:3] = [f,g,h] #DEVNOTE - upperbound exclusive
                frontier.append(newPath)

# Exercise 1: Bidirectional Search
def bidirectional_ucs_save(graph, start, goal):
    """Run bidirectional uniform-cost search
    between start and goal"""
    if start == goal: return []
    # TODO: Test this function!
    # Get it to work with atlanta
    '''Stopping condition: top_f + top_r >= mu, where:
            top_f = length of path from s to top element of forward heap
            top_r = length of reverse path from t to top element of reverse heap
            mu = length of best s-t path seen so far
        '''
    bestPath = []
    mu = float('inf')
    s = start
    t = goal

    frontier_f = PriorityQueue()
    frontier_r = PriorityQueue()
    explored_f = []
    explored_r = []
    explored_path_f = {}
    explored_path_r = {}
    frontier_f.append([0,s])
    frontier_r.append([0,t])
    while frontier_f:
        top_f = frontier_f.pop()
        v = top_f[-1]
        #If path hasn't been explored, then mark explored and save path
        if v not in explored_f:
            explored_f.append(v)
            explored_path_f[v] = top_f
        #If path has been explored, but current pop is cheaper, then save new path
        elif top_f[0] < explored_path_f[v][0]:
            explored_path_f[v] = top_f
        #Do same for reverse
        top_r = frontier_r.pop()
        w = top_r[-1]
        if w not in explored_r:
            explored_r.append(w)
            explored_path_r[w] = top_r
        elif top_r[0] < explored_path_r[w][0]:
            explored_path_r[w] = top_r

        #NOTE: Stopping condition:
        dist_vw = euclidean_dist_heuristic(graph,v,w)
        mu_contender = top_f[0] + top_r[0] + dist_vw
        #NOTE: modifying mu improves results.
        #TODO: submit 1.2 and 1.3
        if mu_contender >= mu:
            # print 'BidirectionalUCSpath: ', bestPath
            return bestPath

        #NOTE: Meet in the middle conditions
        #NOTE: only updates mu when meeting in the middle.
        if v in explored_path_r.keys(): #Met in the middle.
            metPath_f = top_f
            metPath_r = explored_path_r[v]
            #NOTE: met_mu != mu_contender
            met_mu = metPath_f[0] + metPath_r[0]
            if met_mu < mu:
                mu = met_mu
                bestPath = metPath_f[1:-1] + metPath_r[-1:0:-1]

        #from other direction
        elif w in explored_path_f.keys():
            metPath_f = explored_path_f[w]
            metPath_r = top_r
            met_mu = metPath_f[0] + metPath_r[0]
            if met_mu < mu:
                mu = met_mu
                bestPath = metPath_f[1:-1] + metPath_r[-1:0:-1]

        for a in graph[v]:
            if a not in explored_f:
                newPath = list(top_f)
                newPath.append(a)
                d = graph[v][a]['weight']
                newPath[0] += d
                frontier_f.append(newPath)
        for a in graph[w]:
            if a not in explored_r:
                newPath = list(top_r)
                newPath.append(a)
                d = graph[w][a]['weight']
                newPath[0] += d
                frontier_r.append(newPath)
    return bestPath
def bidirectional_ucs(graph, start, goal):
    """Run bidirectional uniform-cost search
    between start and goal"""
    if start == goal: return []
    # TODO: Test this function!
    # Get it to work with atlanta
    '''Stopping condition: get a path met and then run through the rest of the frontier
        '''
    bestPath = []
    mu = float('inf')
    s = start
    t = goal

    frontier_f = PriorityQueue()
    frontier_r = PriorityQueue()
    explored_f = []
    explored_r = []
    explored_path_f = {}
    explored_path_r = {}
    frontier_f.append([0,s])
    frontier_r.append([0,t])
    metFlag = False
    #TODO: make it safe to run through different length frontiers
    while frontier_f.size()>0 or frontier_r.size()>0:
        if frontier_f.size()>0:
            top_f = frontier_f.pop()
            v = top_f[-1]
            #If path hasn't been explored, then mark explored and save path
            if v not in explored_f:
                explored_f.append(v)
                explored_path_f[v] = top_f
            #If path has been explored, but current pop is cheaper, then save new path
            elif top_f[0] < explored_path_f[v][0]:
                explored_path_f[v] = top_f
        #Do same for reverse
        if frontier_r.size()>0:
            top_r = frontier_r.pop()
            w = top_r[-1]
            if w not in explored_r:
                explored_r.append(w)
                explored_path_r[w] = top_r
            elif top_r[0] < explored_path_r[w][0]:
                explored_path_r[w] = top_r

        #NOTE: Meet in the middle conditions
        #NOTE: only updates mu when meeting in the middle.
        if v in explored_path_r.keys(): #Met in the middle.
            metFlag = True
            metPath_f = top_f
            metPath_r = explored_path_r[v]
            #NOTE: met_mu != mu_contender
            mu_contender = metPath_f[0] + metPath_r[0]
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[1:-1] + metPath_r[-1:0:-1]
        #from other direction
        elif w in explored_path_f.keys():
            metFlag = True
            metPath_f = explored_path_f[w]
            metPath_r = top_r
            mu_contender = metPath_f[0] + metPath_r[0]
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[1:-1] + metPath_r[-1:0:-1]

        if metFlag == False:
            for a in graph[v]:
                if a not in explored_f:
                    newPath = list(top_f)
                    newPath.append(a)
                    d = graph[v][a]['weight']
                    newPath[0] += d
                    frontier_f.append(newPath)
            for a in graph[w]:
                if a not in explored_r:
                    newPath = list(top_r)
                    newPath.append(a)
                    d = graph[w][a]['weight']
                    newPath[0] += d
                    frontier_r.append(newPath)
    return bestPath

# Exercise 2: Bidirectional A*
def bidirectional_a_star_save(graph, start, goal, heuristic=euclidean_dist_heuristic):
    """Run bidirectional A* search between
    start and goal."""

    #UNCERTAIN: Message TA if this doesn't work.
    # TODO: Test this function
    # TODO: Test in notebook
    if start == goal: return []
    '''Slides 14-16 for stopping condition:
    stop when top_f + top_r >= mu + p_r(t)
    p_r(t) = .5*(pi_r(t)-pi_f(t))
    pi_f(t) = dist(t,t); pi_r(t) = dist(s,t)
    Therefore:
    stop when top_f + top_r >= mu + .5*dist(s,t)'''
    mu = float('inf')
    s = start
    t = goal

    frontier_f = PriorityQueue()
    frontier_r = PriorityQueue()
    explored_f = []
    explored_r = []
    explored_path_f = {}
    explored_path_r = {}
    #NOTE: add f,g,h to the path
    g = 0
    h = heuristic(graph, s, t)
    f = g + h
    frontier_f.append([f,g,h,s])
    frontier_r.append([f,g,h,t])
    while frontier_f:
        top_f = frontier_f.pop()
        v = top_f[-1]
        explored_f.append(v)
        explored_path_f[v] = top_f
        top_r = frontier_r.pop()
        w = top_r[-1]
        explored_r.append(w)
        explored_path_r[w] = top_r

        #NOTE: Stopping condition:
        #NOTE: Updated Stopping condition
        dist_vw = euclidean_dist_heuristic(graph,v,w)
        mu_contender = top_f[0] + top_r[0] + dist_vw
        dist_st = heuristic(graph,s,t)
        #TODO check with TA that this is correct stopping condition
        rhs = mu + (0.5 * dist_st)
        if mu_contender >= rhs:
            print 'BidirectionalAStarPath: ', bestPath
            return bestPath

        #NOTE: slicing updated
        #NOTE: mu is the same as with bucs
        #NOTE: Meet in the middle conditions
        #NOTE: only updates mu when meeting in the middle.
        if v in explored_path_r.keys(): #Met in the middle.
            metPath_f = top_f
            metPath_r = explored_path_r[v]
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[3:-1] + metPath_r[-1:2:-1]

        #from other direction
        elif w in explored_path_f.keys():
            metPath_f = explored_path_f[w]
            metPath_r = top_r
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[3:-1] + metPath_r[-1:2:-1]

        #NOTE: modified in the style of A*
        for a in graph[v]:
            if a not in explored_f:
                newPath = list(top_f)
                newPath.append(a)
                #update f,g,h
                h = heuristic(graph,a,t)
                gAdd = graph[v][a]['weight']
                g = newPath[1] + gAdd
                f = g + h
                newPath[0:3] = [f,g,h]
                frontier_f.append(newPath)
        for a in graph[w]:
            if a not in explored_r:
                newPath = list(top_r)
                newPath.append(a)
                #update f,g,h
                h = heuristic(graph,a,s) #heuristic reversed
                gAdd = graph[w][a]['weight']
                g = newPath[1] + gAdd
                f = g + h
                newPath[0:3] = [f,g,h]
                frontier_r.append(newPath)
    return [] #In case everything blows up
def bidirectional_a_star(graph, start, goal, heuristic):
    """Run bidirectional uniform-cost search
    between start and goal"""
    if start == goal: return []
    # TODO: Test this function!
    # Get it to work with atlanta
    '''Stopping condition: get a path met and then run through the rest of the frontier
        '''
    bestPath = []
    mu = float('inf')
    s = start
    t = goal

    frontier_f = PriorityQueue()
    frontier_r = PriorityQueue()
    explored_f = []
    explored_r = []
    explored_path_f = {}
    explored_path_r = {}
    #NOTE: add f,g,h to the path
    g = 0
    h = heuristic(graph, s, t)
    f = g + h
    frontier_f.append([f,g,h,s])
    frontier_r.append([f,g,h,t])
    metFlag = False
    #TODO: make it safe to run through different length frontiers
    while frontier_f.size()>0 or frontier_r.size()>0:
        if frontier_f.size()>0:
            top_f = frontier_f.pop()
            v = top_f[-1]
            #If path hasn't been explored, then mark explored and save path
            if v not in explored_f:
                explored_f.append(v)
                explored_path_f[v] = top_f
            #If path has been explored, but current pop is cheaper, then save new path
            elif top_f[0] < explored_path_f[v][0]:
                explored_path_f[v] = top_f
        #Do same for reverse
        if frontier_r.size()>0:
            top_r = frontier_r.pop()
            w = top_r[-1]
            if w not in explored_r:
                explored_r.append(w)
                explored_path_r[w] = top_r
            elif top_r[0] < explored_path_r[w][0]:
                explored_path_r[w] = top_r

        #NOTE: Meet in the middle conditions
        #NOTE: only updates mu when meeting in the middle.
        if v in explored_path_r.keys(): #Met in the middle.
            metFlag = True
            metPath_f = top_f
            metPath_r = explored_path_r[v]
            #NOTE: met_mu != mu_contender
            mu_contender = metPath_f[1] + metPath_r[1]
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[3:-1] + metPath_r[-1:2:-1]
        #from other direction
        elif w in explored_path_f.keys():
            metFlag = True
            metPath_f = explored_path_f[w]
            metPath_r = top_r
            mu_contender = metPath_f[1] + metPath_r[1]
            if mu_contender < mu:
                mu = mu_contender
                bestPath = metPath_f[3:-1] + metPath_r[-1:2:-1]

        if metFlag == False:
            for a in graph[v]:
                if a not in explored_f:
                    newPath = list(top_f)
                    newPath.append(a)
                    #update f,g,h
                    h = heuristic(graph,a,t)
                    gAdd = graph[v][a]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_f.append(newPath)
            for a in graph[w]:
                if a not in explored_r:
                    newPath = list(top_r)
                    newPath.append(a)
                    #update f,g,h
                    h = heuristic(graph,a,s) #heuristic reversed
                    gAdd = graph[w][a]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_r.append(newPath)
    return bestPath

# Exercise 3: Tridirectional UCS Search
#NOTE: Need this function included with tucs
def pathCombine(path1,path2):

    d = path1[0] + path2[0]
    path1 = path1[1:-1]
    path2 = path2[1:]
    path2 = path2[::-1]
    path_comb = [d] + path1 + path2
    return path_comb
def pathDict_to_bestPath(bestPathDict):
    #check for empty list
    if len(bestPathDict['ab']) == 0:
        path1 = bestPathDict['bc']
        path2 = bestPathDict['ac']
    elif len(bestPathDict['bc']) == 0:
        path1 = bestPathDict['ab']
        path2 = bestPathDict['ac']
    elif len(bestPathDict['ac']) == 0:
        path1 = bestPathDict['ab']
        path2 = bestPathDict['bc']
    else:
        pathValList = [bestPathDict['ab'][0],bestPathDict['bc'][0],bestPathDict['ac'][0]]
        maxPos = pathValList.index(max(pathValList))
        if maxPos == 0:
            path1 = bestPathDict['bc']
            path2 = bestPathDict['ac']
        elif maxPos == 1:
            path1 = bestPathDict['ab']
            path2 = bestPathDict['ac']
        elif maxPos == 2:
            path1 = bestPathDict['ab']
            path2 = bestPathDict['bc']

    path1 = path1[1:]
    path2 = path2[1:]

    if path1[-1] == path2[-1]:
        path2rev = path2[::-1]
        bestPath = path1 + path2rev[1:]
    elif path1[0] == path2[0]:
        path2rev = path2[::-1]
        bestPath = path2rev[0:-1] + path1
    elif path1[-1] == path2[0]:
        bestPath = path1+path2[1:]
    elif path1[0] == path2[-1]:
        bestPath = path2+path1[1:]

    return bestPath
def tridirectional_search_save(graph, goals):
    """Run tridirectional uniform-cost search
    between the goals and return the path."""
    # TODO: finish coding
    # TODO: Create test in romania and make sure it passes
    # TODO: run against map_test
    # TODO: submit to bonnie the current state to see what level of credit you can get
    '''Just like bidirectional search but you go from three at a time in round robin fashion
    Stopping condition is when you have two overlap meetings. Then just mash everything together. '''
    #NOTE: conditions for overlapping goals
    '''Conventions: if a path is null then it hasn't been found yet
    Once there has been a meeting in the middle then the path is set
    to whatever nodes met
    When there have been two meetings, then expansion halts and frontiers
    are cleared to see if a better meeting can be found
    Would be open to edge case of allowing best path to change if there was
    a new, better meeting in the middle from the non connected path.
    '''
    bestPath = []
    a = goals[0]
    b = goals[1]
    c = goals[2]
    if a == b == c:
        return bestPath
    bestPathDict = {'ab':[], 'bc':[],'ac':[]}
    #DEVNOTE: could make more efficient by only expanding from A or B when they are equal
    #DEVNOTE: testing could reveal that you need to account for repeat goals. (Just call bidirectional_ucs)

    frontier_a = PriorityQueue()
    frontier_b = PriorityQueue()
    frontier_c = PriorityQueue()
    explored_a = []
    explored_b = []
    explored_c = []
    explored_path_a = {}
    explored_path_b = {}
    explored_path_c = {}
    frontier_a.append([0,a])
    frontier_b.append([0,b])
    frontier_c.append([0,c])

    numMet = 0

    while frontier_a.size()>0 or frontier_b.size()>0 or frontier_c.size()>0:
        if frontier_a.size()>0:
            top_a = frontier_a.pop()
            v_a = top_a[-1]
            if v_a not in explored_a:
                explored_a.append(v_a)
                explored_path_a[v_a] = top_a
            elif top_a[0] < explored_path_a[v_a][0]:
                explored_path_a[v_a] = top_a
        #Do same for b and c
        if frontier_b.size()>0:
            top_b = frontier_b.pop()
            v_b = top_b[-1]
            if v_b not in explored_b:
                explored_b.append(v_b)
                explored_path_b[v_b] = top_b
            elif top_b[0] < explored_path_b[v_b][0]:
                explored_path_b[v_b] = top_b
        if frontier_c.size()>0:
            top_c = frontier_c.pop()
            v_c = top_c[-1]
            if v_c not in explored_c:
                explored_c.append(v_c)
                explored_path_c[v_c] = top_c
            elif top_c[0] < explored_path_c[v_c][0]:
                explored_path_c[v_c] = top_c

        #TODO: Meet in the middle conditions
        #Make sure to account for both directions
        if v_a in explored_path_b.keys() or v_b in explored_path_a.keys():
            if v_a in explored_path_b.keys():
                metPath_a = top_a
                metPath_b = explored_path_b[v_a]
            elif v_b in explored_path_a.keys():
                metPath_a = explored_path_a[v_b]
                metPath_b = top_b
            bestPathContender = pathCombine(metPath_a,metPath_b)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ab'] == []:
                numMet +=1
                bestPathDict['ab'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ab'][0]:
                bestPathDict['ab'] = bestPathContender
        #bc meet
        if v_b in explored_path_c.keys() or v_c in explored_path_b.keys():
            if v_b in explored_path_c.keys():
                metPath_b = top_b
                metPath_c = explored_path_c[v_b]
            elif v_c in explored_path_b.keys():
                metPath_b = explored_path_b[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine(metPath_b,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['bc'] == []:
                numMet +=1
                bestPathDict['bc'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['bc'][0]:
                bestPathDict['bc'] = bestPathContender
        #ac meet
        if v_a in explored_path_c.keys() or v_c in explored_path_a.keys():
            if v_a in explored_path_c.keys():
                metPath_a = top_a
                metPath_c = explored_path_c[v_a]
            elif v_c in explored_path_a.keys():
                metPath_a = explored_path_a[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine(metPath_a,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ac'] == []:
                numMet +=1
                bestPathDict['ac'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ac'][0]:
                bestPathDict['ac'] = bestPathContender

        #NOTE: Expansion - only while paths haven't connected yet.
        if numMet <= 2:
            for act in graph[v_a]:
                if act not in explored_a:
                    newPath = list(top_a)
                    newPath.append(act)
                    d = graph[v_a][act]['weight']
                    newPath[0] += d
                    frontier_a.append(newPath)
            for act in graph[v_b]:
                if act not in explored_b:
                    newPath = list(top_b)
                    newPath.append(act)
                    d = graph[v_b][act]['weight']
                    newPath[0] += d
                    frontier_b.append(newPath)
            for act in graph[v_c]:
                if act not in explored_c:
                    newPath = list(top_c)
                    newPath.append(act)
                    d = graph[v_c][act]['weight']
                    newPath[0] += d
                    frontier_c.append(newPath)
    # print bestPathDict
    bestPath = pathDict_to_bestPath(bestPathDict)

    return bestPath

# Exercise 4: Present an improvement on tridirectional search in terms of nodes explored
def min_euclidean_dist_heuristic(graph, v, goal1, goal2):
    #TODO - play around with different heuristics till it works
    #TODO - try a combined h value.
    h1 = euclidean_dist_heuristic(graph,v,goal1)
    h2 = euclidean_dist_heuristic(graph,v,goal2)
    h = min(h1,h2)
    # h = h1 + h2
    return h

def pathCombine_upgraded(path1,path2):
    #works with f,g,h
    #Only add the distances for the path, ignore h
    d = path1[1] + path2[1]
    path1 = path1[3:-1]
    path2 = path2[3:]
    path2 = path2[::-1]
    path_comb = [d] + path1 + path2
    return path_comb

def tridirectional_upgraded(graph, goals, heuristic=euclidean_dist_heuristic):
    """NOTES:Look on piazza for
    landmarks
    triangle inequality (@17,3.5.2) - prob not necessary,
    shortcuts - ignore
    reach
    a* is probably enough
    What should the heuristic be for a*star since there are two goals?"""
    # TODO: test this function
    bestPath = []
    a = goals[0]
    b = goals[1]
    c = goals[2]
    if a == b == c:
        return bestPath
    bestPathDict = {'ab':[], 'bc':[],'ac':[]}
    #DEVNOTE: could make more efficient by only expanding from A or B when they are equal
    #DEVNOTE: testing could reveal that you need to account for repeat goals. (Just call bidirectional_ucs)

    frontier_a = PriorityQueue()
    frontier_b = PriorityQueue()
    frontier_c = PriorityQueue()
    explored_a = []
    explored_b = []
    explored_c = []
    explored_path_a = {}
    explored_path_b = {}
    explored_path_c = {}
    #NOTE: add f,g,h to the path
    #append with f,g,h
    g = 0
    h = min_euclidean_dist_heuristic(graph,a,b,c)
    f = g + h
    frontier_a.append([f,g,h,a])
    h = min_euclidean_dist_heuristic(graph,b,a,c)
    f = g + h
    frontier_b.append([f,g,h,b])
    h = min_euclidean_dist_heuristic(graph,c,a,b)
    f = g + h
    frontier_c.append([f,g,h,c])

    numMet = 0

    while frontier_a.size()>0 or frontier_b.size()>0 or frontier_c.size()>0:
        if frontier_a.size()>0:
            top_a = frontier_a.pop()
            v_a = top_a[-1]
            if v_a not in explored_a:
                explored_a.append(v_a)
                explored_path_a[v_a] = top_a
            elif top_a[0] < explored_path_a[v_a][0]:
                explored_path_a[v_a] = top_a
        #Do same for b and c
        if frontier_b.size()>0:
            top_b = frontier_b.pop()
            v_b = top_b[-1]
            if v_b not in explored_b:
                explored_b.append(v_b)
                explored_path_b[v_b] = top_b
            elif top_b[0] < explored_path_b[v_b][0]:
                explored_path_b[v_b] = top_b
        if frontier_c.size()>0:
            top_c = frontier_c.pop()
            v_c = top_c[-1]
            if v_c not in explored_c:
                explored_c.append(v_c)
                explored_path_c[v_c] = top_c
            elif top_c[0] < explored_path_c[v_c][0]:
                explored_path_c[v_c] = top_c

        #Meet in the middle conditions
        #Make sure to account for both directions
        if v_a in explored_path_b.keys() or v_b in explored_path_a.keys():
            if v_a in explored_path_b.keys():
                metPath_a = top_a
                metPath_b = explored_path_b[v_a]
            elif v_b in explored_path_a.keys():
                metPath_a = explored_path_a[v_b]
                metPath_b = top_b
            bestPathContender = pathCombine_upgraded(metPath_a,metPath_b)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ab'] == []:
                numMet +=1
                bestPathDict['ab'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ab'][0]:
                bestPathDict['ab'] = bestPathContender
        #bc meet
        if v_b in explored_path_c.keys() or v_c in explored_path_b.keys():
            if v_b in explored_path_c.keys():
                metPath_b = top_b
                metPath_c = explored_path_c[v_b]
            elif v_c in explored_path_b.keys():
                metPath_b = explored_path_b[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine_upgraded(metPath_b,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['bc'] == []:
                numMet +=1
                bestPathDict['bc'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['bc'][0]:
                bestPathDict['bc'] = bestPathContender
        #ac meet
        if v_a in explored_path_c.keys() or v_c in explored_path_a.keys():
            if v_a in explored_path_c.keys():
                metPath_a = top_a
                metPath_c = explored_path_c[v_a]
            elif v_c in explored_path_a.keys():
                metPath_a = explored_path_a[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine_upgraded(metPath_a,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ac'] == []:
                numMet +=1
                bestPathDict['ac'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ac'][0]:
                bestPathDict['ac'] = bestPathContender

        #NOTE: Expansion - only while paths haven't connected yet.
        if numMet <= 2:
            for act in graph[v_a]:
                if act not in explored_a:
                    newPath = list(top_a)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,b,c)
                    gAdd = graph[v_a][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_a.append(newPath)
            for act in graph[v_b]:
                if act not in explored_b:
                    newPath = list(top_b)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,a,c)
                    gAdd = graph[v_b][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_b.append(newPath)
            for act in graph[v_c]:
                if act not in explored_c:
                    newPath = list(top_c)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,a,b)
                    gAdd = graph[v_c][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_c.append(newPath)
    # print bestPathDict
    #NOTE: test function scratch if you're failing at the conversion step
    bestPath = pathDict_to_bestPath(bestPathDict)

    return bestPath
def tridirectional_search(graph, goals):
    #TODO: play around until it stops choosing the wrong leg
    """NOTES:Look on piazza for
    landmarks
    triangle inequality (@17,3.5.2) - prob not necessary,
    shortcuts - ignore
    reach
    a* is probably enough
    What should the heuristic be for a*star since there are two goals?"""
    # TODO: test this function
    bestPath = []
    a = goals[0]
    b = goals[1]
    c = goals[2]
    if a == b == c:
        return bestPath
    bestPathDict = {'ab':[], 'bc':[],'ac':[]}
    #DEVNOTE: could make more efficient by only expanding from A or B when they are equal
    #DEVNOTE: testing could reveal that you need to account for repeat goals. (Just call bidirectional_ucs)

    frontier_a = PriorityQueue()
    frontier_b = PriorityQueue()
    frontier_c = PriorityQueue()
    explored_a = []
    explored_b = []
    explored_c = []
    explored_path_a = {}
    explored_path_b = {}
    explored_path_c = {}
    #NOTE: add f,g,h to the path
    #append with f,g,h
    g = 0
    h = min_euclidean_dist_heuristic(graph,a,b,c)
    f = g + h
    frontier_a.append([f,g,h,a])
    h = min_euclidean_dist_heuristic(graph,b,a,c)
    f = g + h
    frontier_b.append([f,g,h,b])
    h = min_euclidean_dist_heuristic(graph,c,a,b)
    f = g + h
    frontier_c.append([f,g,h,c])

    numMet = 0

    while frontier_a.size()>0 or frontier_b.size()>0 or frontier_c.size()>0:
        if frontier_a.size()>0:
            top_a = frontier_a.pop()
            v_a = top_a[-1]
            if v_a not in explored_a:
                explored_a.append(v_a)
                explored_path_a[v_a] = top_a
            elif top_a[0] < explored_path_a[v_a][0]:
                explored_path_a[v_a] = top_a
        #Do same for b and c
        if frontier_b.size()>0:
            top_b = frontier_b.pop()
            v_b = top_b[-1]
            if v_b not in explored_b:
                explored_b.append(v_b)
                explored_path_b[v_b] = top_b
            elif top_b[0] < explored_path_b[v_b][0]:
                explored_path_b[v_b] = top_b
        if frontier_c.size()>0:
            top_c = frontier_c.pop()
            v_c = top_c[-1]
            if v_c not in explored_c:
                explored_c.append(v_c)
                explored_path_c[v_c] = top_c
            elif top_c[0] < explored_path_c[v_c][0]:
                explored_path_c[v_c] = top_c

        #Meet in the middle conditions
        #Make sure to account for both directions
        if v_a in explored_path_b.keys() or v_b in explored_path_a.keys():
            if v_a in explored_path_b.keys():
                metPath_a = top_a
                metPath_b = explored_path_b[v_a]
            elif v_b in explored_path_a.keys():
                metPath_a = explored_path_a[v_b]
                metPath_b = top_b
            bestPathContender = pathCombine_upgraded(metPath_a,metPath_b)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ab'] == []:
                numMet +=1
                bestPathDict['ab'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ab'][0]:
                bestPathDict['ab'] = bestPathContender
        #bc meet
        if v_b in explored_path_c.keys() or v_c in explored_path_b.keys():
            if v_b in explored_path_c.keys():
                metPath_b = top_b
                metPath_c = explored_path_c[v_b]
            elif v_c in explored_path_b.keys():
                metPath_b = explored_path_b[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine_upgraded(metPath_b,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['bc'] == []:
                numMet +=1
                bestPathDict['bc'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['bc'][0]:
                bestPathDict['bc'] = bestPathContender
        #ac meet
        if v_a in explored_path_c.keys() or v_c in explored_path_a.keys():
            if v_a in explored_path_c.keys():
                metPath_a = top_a
                metPath_c = explored_path_c[v_a]
            elif v_c in explored_path_a.keys():
                metPath_a = explored_path_a[v_c]
                metPath_c = top_c
            bestPathContender = pathCombine_upgraded(metPath_a,metPath_c)
            #Checks if first time meeting and adds to the number of paths met
            if bestPathDict['ac'] == []:
                numMet +=1
                bestPathDict['ac'] = bestPathContender
            #if better path is found then replace
            elif bestPathContender[0] < bestPathDict['ac'][0]:
                bestPathDict['ac'] = bestPathContender

        #NOTE: Expansion - only while paths haven't connected yet.
        if numMet <= 2:
            for act in graph[v_a]:
                if act not in explored_a:
                    newPath = list(top_a)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,b,c)
                    gAdd = graph[v_a][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_a.append(newPath)
            for act in graph[v_b]:
                if act not in explored_b:
                    newPath = list(top_b)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,a,c)
                    gAdd = graph[v_b][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_b.append(newPath)
            for act in graph[v_c]:
                if act not in explored_c:
                    newPath = list(top_c)
                    newPath.append(act)
                    #update f,g,h (x3)
                    h = min_euclidean_dist_heuristic(graph,act,a,b)
                    gAdd = graph[v_c][act]['weight']
                    g = newPath[1] + gAdd
                    f = g + h
                    newPath[0:3] = [f,g,h]
                    frontier_c.append(newPath)
    # print bestPathDict
    #NOTE: test function scratch if you're failing at the conversion step
    bestPath = pathDict_to_bestPath(bestPathDict)

    return bestPath


# Extra Credit: Your best search method for the race
# Loads data from data.pickle and return the data object that is passed to the custom_search method. Will be called only once. Feel free to modify. 
def load_data():
    data = pickle.load(open('./data.pickle', 'rb'))
    return data

def custom_search(graph, goals, data=None):
    raise NotImplementedError