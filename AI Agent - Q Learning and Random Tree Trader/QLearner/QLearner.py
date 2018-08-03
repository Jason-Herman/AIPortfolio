"""
Template for implementing QLearner  (c) 2015 Tucker Balch
"""

import numpy as np
import random as rand

class QLearner(object):

    def __init__(self, \
        num_states=100, \
        num_actions = 4, \
        alpha = 0.2, \
        gamma = 0.9, \
        rar = 0.5, \
        radr = 0.99, \
        dyna = 0, \
        verbose = False):

        self.verbose = verbose
        self.num_states=num_states
        self.alpha=alpha
        self.gamma=gamma
        self.rar=rar
        self.radr=radr
        self.dyna=dyna
        self.num_actions = num_actions
        self.s = 0
        self.a = 0
        self.q = (np.random.random((self.num_states, self.num_actions)) * 2) - 1
        self.tCount = (np.ones([self.num_states, self.num_actions, self.num_states]) * .00001)
        self.t = np.zeros([self.num_states, self.num_actions, self.num_states])
        self.rew = (np.ones([self.num_states, self.num_actions])*-1.0)
    def querysetstate(self, s):
        """
        @summary: Update the state without updating the Q-table
        @param s: The new state
        @returns: The selected action
        """
        self.s = s
        # Choose a random action based on result of rand
        # otherwise, choose the action that makes sense based on the table
        # decay radr
        tempRand = np.random.random(1)
        if tempRand > self.rar:
            # choose an action based on the q table and s
            action = np.argmax(self.q[s])
        elif tempRand <= tempRand:
            # choose a random action
            action = rand.randint(0, self.num_actions - 1)
        self.a = action
        if self.verbose: print "s =", s,"a =",action
        return action

    def query(self, s_prime, r):
        """
        @summary: Update the Q table and return an action
        @param s_prime: The new state
        @param r: The ne state
        @returns: The selected action
        """
        self.q[self.s, self.a] = (1 - self.alpha) * self.q[self.s, self.a] + self.alpha * (
            r + self.gamma * self.q[s_prime, np.argmax(self.q[s_prime])])
        # Choose a random action based on result of rand
        # otherwise, choose the action that makes sense based on the table
        # decay radr
        tempRand = np.random.random(1)
        if tempRand > self.rar:
            # choose an action based on the q table and s_prime
            action = np.argmax(self.q[s_prime])
        elif tempRand <= tempRand:
            # choose a random action
            action = rand.randint(0, self.num_actions - 1)
        # decay
        self.rar = self.rar * self.radr
        if self.dyna > 0:
            # update Model
            self.rew[self.s, self.a] = (1 - self.alpha) * self.rew[self.s, self.a] + self.alpha * r
            self.tCount[self.s, self.a, s_prime] = self.tCount[self.s, self.a, s_prime] + 1
            self.t[self.s, self.a, s_prime] = self.tCount[self.s, self.a, s_prime] / np.sum(
                self.tCount[self.s, self.a, :])
            # renormalize t
            self.t[self.s, self.a, :] /= self.t[self.s, self.a, :].sum()
        # update action and state
        self.s = s_prime
        self.a = action
        if self.verbose: print "s =", s_prime, "a =", action, "r =", r
        if self.dyna > 0:
            # Add in hallucinations to update q
            # choose s,a pairs randomly from t sums with nonzero value
            tSum = np.sum(self.t[:, :, :], axis=2)
            ss, aa = np.nonzero(tSum)
            if len(ss) < self.dyna:
                maxCount = len(ss)
            else:
                maxCount = self.dyna
            ix = np.random.choice(len(ss), self.dyna, replace=True)
            for j in range(0, len(ix)):
                sHal = ss[ix[j]]
                aHal = aa[ix[j]]
                # get s_prime from random weighted selection from t
                s_primeHal = np.random.choice(range(0, self.num_states), p=self.t[sHal, aHal, :])
                # get r from R[s,a]
                rHal = self.rew[sHal, aHal]
                # update Q with sas_primer (the previous equation)
                self.q[sHal, aHal] = (1 - self.alpha) * self.q[sHal, aHal] + self.alpha * (
                    rHal + self.gamma * self.q[s_primeHal, np.argmax(self.q[s_primeHal])])
        return action

    def queryToOptimize(self,s_prime,r):
        """
        @summary: Update the Q table and return an action
        @param s_prime: The new state
        @param r: The ne state
        @returns: The selected action
        """
        self.q[self.s, self.a] = (1 - self.alpha) * self.q[self.s, self.a] + self.alpha * (
        r + self.gamma * self.q[s_prime, np.argmax(self.q[s_prime])])
        #Choose a random action based on result of rand
        #otherwise, choose the action that makes sense based on the table
        #decay radr
        tempRand = np.random.random(1)
        if tempRand > self.rar:
            #choose an action based on the q table and s_prime
            action = np.argmax(self.q[s_prime])
        elif tempRand <= tempRand:
            #choose a random action
            action = rand.randint(0, self.num_actions-1)
        #decay
        self.rar = self.rar * self.radr
        if self.dyna > 0:
            #update Model
            self.rew[self.s,self.a] = (1-self.alpha)*self.rew[self.s,self.a] + self.alpha*r
            self.tCount[self.s,self.a,s_prime] = self.tCount[self.s, self.a, s_prime] + 1
            self.t[self.s, self.a, s_prime] = self.tCount[self.s, self.a, s_prime] / np.sum(self.tCount[self.s, self.a, :])
            #renormalize t
            self.t[self.s,self.a,:] /= self.t[self.s,self.a,:].sum()
        #update action and state
        self.s = s_prime
        self.a = action
        if self.verbose: print "s =", s_prime,"a =",action,"r =",r
        if self.dyna > 0:
            # Add in hallucinations to update q
            #choose s,a pairs randomly from t sums with nonzero value
            tSum = np.sum(self.t[:,:,:],axis=2)
            ss,aa = np.nonzero(tSum)
            ix = np.random.choice(len(ss),self.dyna,replace=True)
            for j in range(0,len(ix)):
                sHal = ss[ix[j]]
                aHal = aa[ix[j]]
                #get s_prime from random weighted selection from t
                s_primeHal = np.random.choice(range(0,self.num_states),p=self.t[sHal, aHal, :])
                #get r from R[s,a]
                rHal = self.rew[sHal,aHal]
                #update Q with sas_primer (the previous equation)
                self.q[sHal, aHal] = (1 - self.alpha) * self.q[sHal, aHal] + self.alpha * (
                    rHal + self.gamma * self.q[s_primeHal, np.argmax(self.q[s_primeHal])])
        return action

    def queryStock(self, s_prime, r):
        """
        @summary: Update the Q table and return an action
        @param s_prime: The new state
        @param r: The ne state
        @returns: The selected action
        """
        # Add in hallucinations
        self.q[self.s, self.a] = (1 - self.alpha) * self.q[self.s, self.a] + self.alpha * (
            r + self.gamma * self.q[s_prime, np.argmax(self.q[s_prime])])
        # Choose a random action based on result of rand
        # otherwise, choose the action that makes sense based on the table
        # decay radr
        tempRand = np.random.random(1)
        if tempRand > self.rar:
            # choose an action based on the q table and s_prime
            action = np.argmax(self.q[s_prime])
        elif tempRand <= tempRand:
            # choose a random action
            action = rand.randint(0, self.num_actions - 1)
        # decay
        self.rar = self.rar * self.radr
        # update action and state
        self.s = s_prime
        self.a = action
        if self.verbose: print "s =", s_prime, "a =", action, "r =", r
        return action

if __name__=="__main__":
    print "Remember Q from Star Trek? Well, this isn't him"