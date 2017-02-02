#!/usr/bin/env python
# -*- coding: utf-8 -*-

# This file is your main submission that will be graded against. Only copy-paste
# code on the relevant classes included here from the IPython notebook. Do not
# add any classes or functions to this file that are not part of the classes
# that we want.

# Submission Class 1
class OpenMoveEvalFn():
    def score(self, game, maximizing_player_turn=True):
        inactiveMovesList1 = game.get_opponent_moves().values()[0]
        inactiveMovesList2 = game.get_opponent_moves().values()[1]
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        #get the intersection of two lists, look on stack overflow.
        list(set().union(activeMovesList1,activeMovesList2))
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
        eval_func = len(combinedMovesActive) - len(combinedMovesInactive)
        return eval_func
        
# Submission Class 2
class CustomEvalFn():
    """Custom evaluation function that acts
    however you think it should. This is not
    required but highly encouraged if you
    want to build the best AI possible."""
    def score(self, game, maximizing_player_turn=True):
        # TODO: implement to look for partition moves, and possibly do initial placement
        return eval_func

class CustomPlayer():
    def __init__(self,  search_depth=2, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 10 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.alphabetaID(game,time_left, depth=self.search_depth)
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)

    miniCount = 0
    def minimax(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - raise exception if out of time
        if time_left() < self.time_safety_factor:
            print 'time_left at error:', time_left()
            raise ValueError(self.IDMessage.encode('utf-8'))
        self.miniCount += 1
        # print self.miniCount

        # if depth == 3:
        #     print '!!NEW MOVE!!'
        # print 'minimax call'
        # print game.print_board()
        #terminal test
        activeMovesList = game.get_legal_moves()

        # if activeMovesList == {11: [(2, 1)], 12: [(2, 3), (3, 3), (0, 3), (0, 2)]}:
        #     print 'debug here'
        #
        # print 'maximizing player', maximizing_player
        # print 'active moves list:', activeMovesList
        terminal = False
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))

        #logic to check how many queens have been placed and return a move accordingly.
        #Check for openning moves
        board_size = 7
        spaces = board_size**2
        numOpenMoves1 = len(activeMovesList1)
        numOpenMoves2 = len(activeMovesList2)
        if numOpenMoves1 == spaces and numOpenMoves2 == spaces: #First move
            best_move = (3,4)
            best_queen = 11
            best_val = 0
            return best_move,best_queen,best_val
        elif numOpenMoves1 == spaces-1 and numOpenMoves2 == spaces-1: #Second move
            best_queen = 21
            best_val = 0
            #check if move is legal
            if game.move_is_legal(3,3):
                best_move = (3,3)
            else:
                best_move = (3,4)
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-2: #third move
            best_queen = 12
            best_val = 0
            #check if move is legal
            if game.move_is_legal(2,2):
                best_move = (2,2)
            else:
                best_move = (4,2)
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-3: #fourth move
            best_queen = 22
            best_val = 0
            #check if move is legal
            if game.move_is_legal(4,1):
                best_move = (4,1)
            elif game.move_is_legal(2,1):
                best_move = (2,1)
            else:
                best_move = (1,1)
            return best_move,best_queen,best_val

        if len(combinedMovesActive) ==0:
            terminal = True
            # print 'terminal'
        # return utility at leaf
        if depth == 0 or terminal:
            # print 'depth 0'
            # return self.eval_fn.score(game)
            if maximizing_player == True:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = 1*len(combinedMovesActive) - 2*len(combinedMovesInactive) #put this in. It wrecks.
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = 1*len(combinedMovesActive) - 2*len(combinedMovesInactive)
                v = -v
            #need to return the moves, queen, and val.
            dummy1 = 1
            dummy2 = 2
            return dummy1,dummy2,v

        if maximizing_player == True:
            best_val = -999
            #iterate through keys of active move list to deal with which player is moving
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    #Make sure you're actually pulling v out of the function, not best move.
                    bm,bq,v = self.minimax(child, time_left, depth-1, alpha, beta, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                    if best_val > alpha:
                        alpha = best_val
                    if beta <= alpha:
                        break #TODO: break out again
                        # print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, alpha, beta, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                    if beta < best_val:
                        beta = best_val
                    if beta <= alpha:
                        break
            return best_move,best_queen,best_val

    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - raise exception if out of time
        if time_left() < self.time_safety_factor:
            print 'time_left at error:', time_left()
            raise ValueError(self.IDMessage.encode('utf-8'))

        #DEPRECATE
        # if depth == 3:
        #     print '!!NEW MOVE!!'
        # print 'alphabeta call'
        # print game.print_board()

        #terminal test
        activeMovesList = game.get_legal_moves()

        #DEPRECATE
        # print 'maximizing player', maximizing_player
        # print 'active moves list:', activeMovesList

        terminal = False
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))

        #logic to check how many queens have been placed and return a move accordingly.
        #Check for openning moves
        board_size = 7
        spaces = board_size**2
        numOpenMoves1 = len(activeMovesList1)
        numOpenMoves2 = len(activeMovesList2)
        if numOpenMoves1 == spaces and numOpenMoves2 == spaces: #First move
            best_move = (3,4)
            best_queen = 11
            best_val = 0
            return best_move,best_queen,best_val
        elif numOpenMoves1 == spaces-1 and numOpenMoves2 == spaces-1: #Second move
            best_queen = 21
            best_val = 0
            #check if move is legal
            if game.move_is_legal(3,3):
                best_move = (3,3)
            else:
                best_move = (3,4)
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-2: #third move
            best_queen = 12
            best_val = 0
            #check if move is legal
            if game.move_is_legal(2,2):
                best_move = (2,2)
            else:
                best_move = (4,2)
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-3: #fourth move
            best_queen = 22
            best_val = 0
            #check if move is legal
            if game.move_is_legal(4,1):
                best_move = (4,1)
            elif game.move_is_legal(2,1):
                best_move = (2,1)
            else:
                best_move = (1,1)
            return best_move,best_queen,best_val


        if len(combinedMovesActive) ==0:
            terminal = True

            #DEPRECATE
            # print 'terminal'

        # return utility at leaf
        if depth == 0 or terminal:

            #DEPRECATE
            # print 'depth 0'
            # return self.eval_fn.score(game)

            if maximizing_player == True:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))

                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))

                #DEPRECATE
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                #TODO: customize eval function here.
                v = 1*len(combinedMovesActive) - 1*len(combinedMovesInactive) #Does especially well
                # v = 2*len(combinedMovesActive) - len(combinedMovesInactive) #Beats minimax quite well.
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))

                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))

                #DEPRECATE
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                #TODO: customize eval function here.
                v = 1*len(combinedMovesActive) - 1*len(combinedMovesInactive)
                # v = 2*len(combinedMovesActive) - len(combinedMovesInactive)
                v = -v
            dummy1 = 1
            dummy2 = 2
            return dummy1,dummy2,v

        if maximizing_player == True:
            best_val = -999
            breakflag = False
            for queen in activeMovesList:
                if breakflag == True:
                    break
                for move in activeMovesList[queen]:
                    if breakflag == True:
                        break
                    child = game.forecast_move(move,queen)
                    #Make sure you're actually pulling v out of the function, not best move.
                    bm,bq,v = self.alphabeta(child, time_left, depth-1, alpha, beta, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v

                        #DEPRECATE
                        # print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val

                    if v > alpha:
                        alpha = v
                    if beta <= alpha:
                        breakflag = True
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            breakflag = False
            for queen in activeMovesList:
                if breakflag == True:
                    break
                for move in activeMovesList[queen]:
                    if breakflag == True:
                        break
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.alphabeta(child, time_left, alpha, beta, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                    if v < beta:
                        beta = v
                    if beta <= alpha:
                        breakflag = True
            return best_move,best_queen,best_val

    moveCount = 0
    def alphabetaID(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        self.moveCount+=1
        print 'moveCount:',self.moveCount
        #initialization - to keep track of best move while doing iterative deepening.
        best_best_val = -999 #float(-inf)
        best_best_queen = 0
        best_best_move = (-2,-2)
        depth = 0
        timeout = False
        TL = time_left()
        previous_time = TL
        while timeout == False:
            try:
                depth += 1
                if depth >=49:
                    break
                best_move,best_queen,best_val = self.minimax(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val,'  queen',best_queen,'   time_left', time_left()
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Tester Code: delete
            # if depth == 2:
            #     timeout = True
        return best_best_move, best_best_queen, best_best_val


