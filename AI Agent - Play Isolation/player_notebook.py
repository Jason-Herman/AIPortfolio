#
# # coding: utf-8
'''
# # In[ ]:
#
# # Isolation
#
# This IPython notebook contains the skeletons of the player class and eval function classes that you need to fill out. In addition, we have included the `RandomPlayer` and `HumanPlayer` classes for you to test against.
#
# ## Submitting
#
# When you are ready to submit, copy code from the following classes into the attached `player_submission.py`:
#
# 1. OpenMoveEvalFn
# 1. CustomEvalFn
# 1. CustomPlayer
#
# Please do not copy any code that is not part of those classes. You may be tempted to simply export a python file from this notebook and use that as a submission; please **DO NOT** do that. We need to be certain that code unprotected by a *main* test (any tests that you might want to write) do not get accidentally executed.
#
#
# # ## Helper Player classes
# #
# # We include 2 player types for you to test against locally:
# #
# # - `RandomPlayer` - chooses a legal move randomly from among the available legal moves
# # - `HumanPlayer` - allows *YOU* to play against the AI
# #
# # **DO NOT** submit.
# #
# # You are however free to change these classes as you see fit. Know that any changes you make will be solely for the benefit of your own tests.
#
# # In[3]:
'''
from random import randint, seed

class RandomPlayer():
    """Player that chooses a move randomly."""
    # seed(9002)     # converted so that it is not random
    def move(self, game, legal_moves, time_left):
        if not legal_moves: return (-1,-1)        
        num=randint(game.__active_players_queen1__,game.__active_players_queen2__)
        if not len(legal_moves[num]):
            num = game.__active_players_queen1__ if num == game.__active_players_queen2__ else game.__active_players_queen2__
            if not len(legal_moves[num]):
                return (-1,-1),num
        
        moves=legal_moves[num][randint(0,len(legal_moves[num])-1)]
        return moves,num


class HumanPlayer():
    """Player that chooses a move according to
    user's input."""
    def move(self, game, legal_moves, time_left):
        i=0
        choice = {}
        if not len(legal_moves[game.__active_players_queen1__]) and not len(legal_moves[game.__active_players_queen2__]):
            return None, None
        for queen in legal_moves:
                for move in legal_moves[queen]:        
                    choice.update({i:(queen,move)})
                    print('\t'.join(['[%d] q%d: (%d,%d)'%(i,queen,move[0],move[1])] ))
                    i=i+1
        
        
        valid_choice = False
        while not valid_choice:
            try:
                index = int(input('Select move index:'))
                valid_choice = 0 <= index < i

                if not valid_choice:
                    print('Illegal move! Try again.')
            
            except ValueError:
                print('Invalid index! Try again.')
        
        return choice[index][1],choice[index][0]

'''
# ## Evaluation Functions
#
# These functions will inform the value judgements your AI will make when choosing moves. There are 2 classes:
#
# - `OpenMoveEvalFn` - Scores the maximum number of available moves open for computer player minus the maximum number of moves open for opponent player. All baseline tests will use this function. **This is mandatory**
# - `CustomEvalFn` - You are encouraged to create your own evaluation function here.
#
# **DO** submit code within the classes (and only that within the classes).
#
# ### Tips
#
# 1. You may write additional code within each class. However, we will only be invoking the `score()` function. You may not change the signature of this function.
# 1. When writing additional code to test, try to do so in separate cells. It allows for independent test execution and you can be sure that *all* the code within the EvalFn cells belong only to the EvalFn classes

# In[84]:
'''
class OpenMoveEvalFn():
    #NOTE: Tested, rock solid.
    """Evaluation function that outputs a
    score equal to how many moves are open
    for AI player on the board 
    minus the moves open for opponent player."""
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

class CustomEvalFn():
    """Custom evaluation function that acts
    however you think it should. This is not
    required but highly encouraged if you
    want to build the best AI possible."""
    def score(self, game, maximizing_player_turn=True):
        return eval_func

"""Example test you can run
to make sure your basic evaluation
function works."""

'''from isolation import Board

if __name__ == "__main__":
    sample_board = Board(RandomPlayer(),RandomPlayer())
    # setting up the board as though we've been playing
    sample_board.move_count = 4
    sample_board.__board_state__ = [
                [11,0,0,0,21,0,0],
                [0,0,0,0,0,0,0],
                [0,0,22,0,0,0,0],
                [0,0,0,0,0,0,0],
                [0,0,0,0,0,12,0],
                [0,0,0,0,0,0,0],
                [0,0,0,0,0,0,0]
    ]
#     sample_board.__board_state__ = [
#                 [11,0,0,0,0,0,0],
#                 [21,22,0,0,0,0,0],
#                 [0,0,0,0,0,0,0],
#                 [0,0,0,0,0,0,0],
#                 [0,0,0,0,0,0,0],
#                 [0,0,0,0,0,0,0],
#                 [0,0,0,0,0,0,12]
#     ]
    sample_board.__last_queen_move__ = {sample_board.queen_11: (0,0), sample_board.queen_12: (4,5),
                                        sample_board.queen_21: (0,4), sample_board.queen_22: (2,2)}
    h = OpenMoveEvalFn()
    print('This board has a score of %s.'%(h.score(sample_board)))
    # the answer should be maximum computer player moves - maximum opponent player moves available.
    # correct answer is -1'''



## CustomPlayer

# This is the meat of the assignment. A few notes about the class:
#
# - You are not permitted to change the function signatures of any of the provided methods.
# - You are permitted to change the default values within the function signatures provided. In fact, when you have your custom evaluation function, you are encouraged to change the default values for `__init__` to use the new eval function.
# - You are free change the contents of each of the provided methods. When you are ready with `alphabeta()`, for example, you are encouraged to update `move()` to use that function instead.
# - You are free to add more methods to the class.
# - You may not create additional external functions and classes that are referenced from within this class.
#
# **DO** submit the code within this class (and only the code within this class).


'''
class CustomPlayerAB():
    def __init__(self,  search_depth=2, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 3000 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.alphabeta(game,time_left, depth=self.search_depth) #NOTE: switch back to minimax if you go nuclear
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)

    def maxValue(self, game, time_left, depth):
        #if terminal-test(state) then return eval_fn(state)
        if depth == 0:
            val = self.eval_fn.score(game)
            return val
        # for a, s in Successors(state) do val <0 MAX(v,MIN-VALUE(s))
        best_val = -999 #Essentially float('-inf')
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
        return best_val

    def minValue(self, game, time_left, depth):
        #if terminal-test(state) then return eval_fn(state)
        if depth == 0:
            val = self.eval_fn.score(game)
            val = val*-1 #Accounts for the perspective shift
            return val
        # for a, s in Successors(state) do val <- MIN(v,MAX-VALUE(s))
        best_val = 999
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.maxValue(forecastBoard, time_left, depth-1)
            if val<best_val:
                best_val = val
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.maxValue(forecastBoard, time_left, depth-1)
            if val<best_val:
                best_val = val
        return best_val

    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):
        #Note: missing terminal states.
        if self.player == 0:
            if game.get_inactive_players_queen()[0] == 11:
                self.player = 2
            else:
                self.player = 1
        best_val = -999
        best_queen = 0
        best_move = (-2,-2)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            forecastBoard = game.forecast_move(move, 21)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player == 2:
                    best_queen = 21
                else:
                    best_queen = 11
                best_move = move
        for move in activeMovesList2:
            forecastBoard = game.forecast_move(move, 22)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player ==2:
                    best_queen = 22
                else:
                    best_queen = 12
                best_move = move
        return best_move,best_queen, best_val

    def abMaxValue(self, game, time_left, depth, alpha, beta):
        #ID - Raise exception if out of time
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        #if terminal-test(state) then return eval_fn(state)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        terminal = False
        if len(combinedMovesActive) ==0:
            terminal = True
        if depth == 0 or terminal:
            val = self.eval_fn.score(game)
            return val
        # for a, s in Successors(state) do val <0 MAX(v,MIN-VALUE(s))
        best_val = -999 #Basically float(-inf)
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
            #WIKICODE
            alpha = max(alpha, best_val)
            if beta <= alpha:
                break
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
            #WIKICODE
            alpha = max(alpha, best_val)
            if beta <= alpha:
                break
        return best_val

    def abMinValue(self, game, time_left, depth, alpha, beta):
        #ID - Raise exception if out of time
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        #if terminal-test(state) then return eval_fn(state)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        terminal = False
        if len(combinedMovesActive) ==0:
            terminal = True
        if depth == 0 or terminal:
            val = self.eval_fn.score(game)
            val = val*-1 #Accounts for the perspective shift
            return val
        # for a, s in Successors(state) do val <- MIN(v,MAX-VALUE(s))
        best_val = 999 #float('inf')
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.abMaxValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val<best_val:
                best_val = val
            #WIKI
            beta = min(beta,best_val)
            if beta<=alpha:
                break
        #return v - This is done after the second move list
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.abMaxValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val<best_val:
                best_val = val
            #WIKI
            beta = min(beta,best_val)
            if beta<=alpha:
                break
        return best_val #return v

    def alphabetaMain(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - Raise exception if out of time - May not be needed here.
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        if self.player == 0:
            if game.get_inactive_players_queen()[0] == 11:
                self.player = 2
            else:
                self.player = 1
        #initialize
        best_val = -999 #float(-inf)
        best_queen = 0
        best_move = (-2,-2)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        #No terminal logic here because if you have no moves when you're calling this function then you have already lost.
        for move in activeMovesList1:
            forecastBoard = game.forecast_move(move, 21)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player == 2:
                    best_queen = 21
                else:
                    best_queen = 11
                best_move = move
        for move in activeMovesList2:
            forecastBoard = game.forecast_move(move, 22)
            val = self.abMinValue(forecastBoard, time_left, depth-1,alpha,beta)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player ==2:
                    best_queen = 22
                else:
                    best_queen = 12
                best_move = move
        return best_move,best_queen, best_val

    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #call alphabetaMain
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
                best_move,best_queen,best_val = self.alphabetaMain(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Removes ID
            if depth == 2:
                timeout = True
        return best_best_move, best_best_queen, best_best_val

class CustomPlayerID():
    def __init__(self,  search_depth=2, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 100 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.alphabeta(game,time_left, depth=self.search_depth) #NOTE: switch back to minimax if you go nuclear
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)

    def maxValue(self, game, time_left, depth):
        #if terminal-test(state) then return eval_fn(state)
        if depth == 0:
            val = self.eval_fn.score(game)
            return val
        # for a, s in Successors(state) do val <0 MAX(v,MIN-VALUE(s))
        best_val = -999 #Essentially float('-inf')
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
        return best_val

    def minValue(self, game, time_left, depth):
        #if terminal-test(state) then return eval_fn(state)
        if depth == 0:
            val = self.eval_fn.score(game)
            val = val*-1 #Accounts for the perspective shift
            return val
        # for a, s in Successors(state) do val <- MIN(v,MAX-VALUE(s))
        best_val = 999
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.maxValue(forecastBoard, time_left, depth-1)
            if val<best_val:
                best_val = val
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.maxValue(forecastBoard, time_left, depth-1)
            if val<best_val:
                best_val = val
        return best_val

    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):
        #Note: missing terminal states.
        if self.player == 0:
            if game.get_inactive_players_queen()[0] == 11:
                self.player = 2
            else:
                self.player = 1
        best_val = -999
        best_queen = 0
        best_move = (-2,-2)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        for move in activeMovesList1:
            forecastBoard = game.forecast_move(move, 21)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player == 2:
                    best_queen = 21
                else:
                    best_queen = 11
                best_move = move
        for move in activeMovesList2:
            forecastBoard = game.forecast_move(move, 22)
            val = self.minValue(forecastBoard, time_left, depth-1)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player ==2:
                    best_queen = 22
                else:
                    best_queen = 12
                best_move = move
        return best_move,best_queen, best_val

    def abMaxValue(self, game, time_left, depth, alpha, beta):
        #ID - Raise exception if out of time
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        #if terminal-test(state) then return eval_fn(state)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        terminal = False
        if len(combinedMovesActive) ==0:
            terminal = True
        if depth == 0 or terminal:
            val = self.eval_fn.score(game)
            return val
        # for a, s in Successors(state) do val <0 MAX(v,MIN-VALUE(s))
        best_val = -999 #Basically float(-inf)
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
            #WIKICODE
            alpha = max(alpha, best_val)
            if beta <= alpha:
                break
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
            #WIKICODE
            alpha = max(alpha, best_val)
            if beta <= alpha:
                break
        return best_val

    def abMinValue(self, game, time_left, depth, alpha, beta):
        #ID - Raise exception if out of time
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        #if terminal-test(state) then return eval_fn(state)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        terminal = False
        if len(combinedMovesActive) ==0:
            terminal = True
        if depth == 0 or terminal:
            val = self.eval_fn.score(game)
            val = val*-1 #Accounts for the perspective shift
            return val
        # for a, s in Successors(state) do val <- MIN(v,MAX-VALUE(s))
        best_val = 999 #float('inf')
        for move in activeMovesList1:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 21)
            else:
                forecastBoard = game.forecast_move(move, 11)
            val = self.abMaxValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val<best_val:
                best_val = val
            #WIKI
            beta = min(beta,best_val)
            if beta<=alpha:
                break
        #return v - This is done after the second move list
        for move in activeMovesList2:
            if self.player == 2:
                forecastBoard = game.forecast_move(move, 22)
            else:
                forecastBoard = game.forecast_move(move, 12)
            val = self.abMaxValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val<best_val:
                best_val = val
            #WIKI
            beta = min(beta,best_val)
            if beta<=alpha:
                break
        return best_val #return v

    def alphabetaMain(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - Raise exception if out of time - May not be needed here.
        if time_left() < self.time_safety_factor:
            raise ValueError(self.IDMessage.encode('utf-8'))
        if self.player == 0:
            if game.get_inactive_players_queen()[0] == 11:
                self.player = 2
            else:
                self.player = 1
        #initialize
        best_val = -999 #float(-inf)
        best_queen = 0
        best_move = (-2,-2)
        # Populate moves array for active player
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        #No terminal logic here because if you have no moves when you're calling this function then you have already lost.
        for move in activeMovesList1:
            forecastBoard = game.forecast_move(move, 21)
            val = self.abMinValue(forecastBoard, time_left, depth-1, alpha, beta)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player == 2:
                    best_queen = 21
                else:
                    best_queen = 11
                best_move = move
        for move in activeMovesList2:
            forecastBoard = game.forecast_move(move, 22)
            val = self.abMinValue(forecastBoard, time_left, depth-1,alpha,beta)
            if val>best_val:
                best_val = val
                #set best_queen based on the current player
                if self.player ==2:
                    best_queen = 22
                else:
                    best_queen = 12
                best_move = move
        return best_move,best_queen, best_val

    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
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
                best_move,best_queen,best_val = self.alphabetaMain(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val,'  queen',best_queen
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Tester Code: delete
            # if depth == 2:
            #     timeout = True
        return best_best_move, best_best_queen, best_best_val
'''
class CustomPlayerMM():
    def __init__(self,  search_depth=3, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 100 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.minimax(game,time_left, depth=self.search_depth)
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)

    miniCount = 0
    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):
        #TODO add random first moves.
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
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
                    bm,bq,v = self.minimax(child, time_left, depth-1, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                        # print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
            return best_move,best_queen,best_val

    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - raise exception if out of time
        if time_left() < self.time_safety_factor:
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

                v = len(combinedMovesActive) - len(combinedMovesInactive)
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

                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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

    def alphabetaID(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
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
                best_move,best_queen,best_val = self.alphabeta(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                if depth < 49:
                    print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val,'  queen',best_queen
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Tester Code: delete
            # if depth == 2:
            #     timeout = True
        return best_best_move, best_best_queen, best_best_val
class CustomPlayerAB():
    #This seems to be broken.
    def __init__(self,  search_depth=3, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 100 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.alphabeta(game,time_left, depth=self.search_depth)
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)

    miniCount = 0
    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):

        self.miniCount += 1
        print self.miniCount

        if depth == 3:
            print '!!NEW MOVE!!'
        print 'minimax call'
        print game.print_board()
        #terminal test
        activeMovesList = game.get_legal_moves()

        if activeMovesList == {11: [(2, 1)], 12: [(2, 3), (3, 3), (0, 3), (0, 2)]}:
            print 'debug here'

        print 'maximizing player', maximizing_player
        print 'active moves list:', activeMovesList
        terminal = False
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        if len(combinedMovesActive) ==0:
            terminal = True
            print 'terminal'
        # return utility at leaf
        if depth == 0 or terminal:
            print 'depth 0'
            # return self.eval_fn.score(game)
            if maximizing_player == True:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
                    bm,bq,v = self.minimax(child, time_left, depth-1, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                        print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
            return best_move,best_queen,best_val

    alphaCount = 0
    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        self.alphaCount += 1
        # print self.alphaCount
        print depth
        #ID - raise exception if out of time
        # if time_left() < self.time_safety_factor:
        #     raise ValueError(self.IDMessage.encode('utf-8'))

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

                v = len(combinedMovesActive) - len(combinedMovesInactive)
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

                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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

    def alphabetaID(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
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
                best_move,best_queen,best_val = self.alphabeta(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                if depth < 49:
                    print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val,'  queen',best_queen
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Tester Code: delete
            # if depth == 2:
            #     timeout = True
        return best_best_move, best_best_queen, best_best_val
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
    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):

        self.miniCount += 1
        print self.miniCount

        if depth == 3:
            print '!!NEW MOVE!!'
        print 'minimax call'
        print game.print_board()
        #terminal test
        activeMovesList = game.get_legal_moves()

        if activeMovesList == {11: [(2, 1)], 12: [(2, 3), (3, 3), (0, 3), (0, 2)]}:
            print 'debug here'

        print 'maximizing player', maximizing_player
        print 'active moves list:', activeMovesList
        terminal = False
        activeMovesList1 = game.get_legal_moves().values()[0]
        activeMovesList2 = game.get_legal_moves().values()[1]
        combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
        if len(combinedMovesActive) ==0:
            terminal = True
            print 'terminal'
        # return utility at leaf
        if depth == 0 or terminal:
            print 'depth 0'
            # return self.eval_fn.score(game)
            if maximizing_player == True:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
                    bm,bq,v = self.minimax(child, time_left, depth-1, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                        print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
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
                best_move,best_queen,best_val = self.alphabeta(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
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
class CustomPlayerMod():
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
    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):
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
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
                    bm,bq,v = self.minimax(child, time_left, depth-1, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                        # print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
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
                best_move,best_queen,best_val = self.minimax(game, time_left, depth, maximizing_player) #variable assignment looks okay
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
class CustomPlayerModAB():
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
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
class CustomPlayerModABEval():
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
class CustomPlayerMMRandom():
    def __init__(self,  search_depth=3, eval_fn=OpenMoveEvalFn()):
        # if you find yourself with a superior eval function, update the
        # default value of `eval_fn` to `CustomEvalFn()`
        self.eval_fn = eval_fn
        self.search_depth = search_depth #Could be used before using time limit with minimax.
        self.player = 0 #For setting first and second player
        self.time_safety_factor = 100 #Tweak for faster testing
        self.IDMessage = 'ID out of time'


    def move(self, game, legal_moves, time_left):
        best_move,best_queen, utility = self.minimax(game,time_left, depth=self.search_depth)
        return best_move, best_queen


    def utility(self, game):
        #Build out later if you want to modify eval_fn here rather than in custom eval.
        return self.eval_fn.score(game)
    from random import randint
    miniCount = 0
    def minimax(self, game, time_left, depth=float("inf"), maximizing_player=True):
        #TODO add random first moves.
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
            best_move = (randint(0,6),randint(0,6))
            best_queen = 11
            best_val = 0
            return best_move,best_queen,best_val
        elif numOpenMoves1 == spaces-1 and numOpenMoves2 == spaces-1: #Second move
            best_queen = 21
            best_val = 0
            best_move = (randint(0,6),randint(0,6))
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-2: #third move
            best_queen = 12
            best_val = 0
            #check if move is legal
            best_move = (randint(0,4),randint(0,4))
            return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-3: #fourth move
            best_queen = 22
            best_val = 0
            #check if move is legal
            best_move = (randint(0,6),randint(0,6))
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
                v = len(combinedMovesActive) - len(combinedMovesInactive)
            if maximizing_player == False:
                activeMovesList1 = game.get_legal_moves().values()[0]
                activeMovesList2 = game.get_legal_moves().values()[1]
                combinedMovesActive = list(set().union(activeMovesList1,activeMovesList2))
                # print 'combinedMovesActive',combinedMovesActive, 'len', len(combinedMovesActive)
                inactiveMovesList1 = game.get_opponent_moves().values()[0]
                inactiveMovesList2 = game.get_opponent_moves().values()[1]
                combinedMovesInactive = list(set().union(inactiveMovesList1,inactiveMovesList2))
                # print 'combinedMovesInActive',combinedMovesInactive, 'len', len(combinedMovesInactive)
                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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
                    bm,bq,v = self.minimax(child, time_left, depth-1, False)
                    #if value is max, then keep it.
                    if v > best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
                        # print 'update','best_queen',best_queen,'best_move',best_move,'best_val',best_val
            return best_move,best_queen,best_val

        elif maximizing_player == False:
            best_val = 999
            for queen in activeMovesList:
                for move in activeMovesList[queen]:
                    child = game.forecast_move(move,queen)
                    bm,bq,v = self.minimax(child, time_left, depth-1, True)
                    # if value is min, then keep it
                    if v < best_val:
                        best_queen = queen
                        best_move = move
                        best_val = v
            return best_move,best_queen,best_val

    def alphabeta(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
        #ID - raise exception if out of time
        if time_left() < self.time_safety_factor:
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

                v = len(combinedMovesActive) - len(combinedMovesInactive)
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

                v = len(combinedMovesInactive) - len(combinedMovesActive) #Flipping doesn't seem to work
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

    def alphabetaID(self, game, time_left, depth=float("inf"), alpha=float("-inf"), beta=float("inf"), maximizing_player=True):
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
                best_move,best_queen,best_val = self.alphabeta(game, time_left, depth, alpha, beta, maximizing_player) #variable assignment looks okay
                #update variables
                # if best_val > best_best_val: #Probably delete, don't care as much about val
                best_best_move = best_move
                best_best_queen = best_queen
                best_best_val = best_val
                if depth < 49:
                    print 'depth', depth,'  move',best_move,'   best_move',best_best_move,'  val',best_val,'   best_val',best_best_val,'  queen',best_queen
                # print depth,': depth ', best_best_move,': best_best_move ', best_best_val,': best_best_val ', best_val,': best_val'
            except ValueError as err:
                print err.args #Comment out printing just in case
                break
            #Get time used by last move

            #Tester Code: delete
            # if depth == 2:
            #     timeout = True
        return best_best_move, best_best_queen, best_best_val
class CustomPlayerModOpenMove():
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

    def getCenterMove(self):
        best_move = (-2,-2) #result if it doesn't find anything that works.
        best_queen = 00
        best_val = 0
        worked = False

        #TODO:get active player so you can check only the other queens.
        player = self.player
        #TODO:get last queen move from board
        if player == 1:
            oppQueens = ['queen21','queen22']
        else:
            oppQueens = ['queen11','queen12']
        for oppQueen in oppQueens:
            last_move = game.__last_queen_move__[oppQueen]
            
            #TODO: check if move is in list, then set worked to true
            get_legal_moves_of_queen1()



        return best_move, best_queen, best_val, worked

    player = 0 #initialize
    moveDict = {(0,0):(1,1)}#TODO: finish filling out
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




            best_move, best_queen, best_val, worked = self.getCenterMove()
            if worked:
                return best_move, best_queen, best_val
            else:
                best_queen = 21
                best_val = 0
                #check if move is legal
                if game.move_is_legal(3,3):
                    best_move = (3,3)
                else:
                    best_move = (3,4)
                return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-2: #third move
            best_move, best_queen, best_val, worked = self.getCenterMove()
            if worked:
                return best_move, best_queen, best_val
            else:
                best_queen = 12
                best_val = 0
                #check if move is legal
                if game.move_is_legal(2,2):
                    best_move = (2,2)
                else:
                    best_move = (4,2)
                return best_move,best_queen,best_val
        elif numOpenMoves1 == 0 and numOpenMoves2 == spaces-3: #fourth move
            best_move, best_queen, best_val, worked = self.getCenterMove()
            if worked:
                return best_move, best_queen, best_val
            else:
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
        #get move count from 5 to 8 (10) and perform centering
        cnt = game.move_count
        if cnt > 4 and cnt < 9: #TODO: tweak here for testing
            best_move, best_queen, best_val, worked = self.getCenterMove()
            if worked:
                return best_move, best_queen, best_val



        #regular scheduled programing
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
        #Get player
        if self.player == 0:
            if game.get_legal_moves.keys()[0] == 11:
                self.player = 1
            else:
                self.player = 2

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
"""Example test to make sure
your minimax works, using the
#computer_player_moves - opponent_moves evaluation function."""
'''from isolation import Board, game_as_text
# # NOTES: Player 1 is stationary, the player 2 moves don't appear to be legal.
if __name__ == "__main__":
    # create dummy 3x3 board

    p1 = RandomPlayer()
    # p1 = CustomPlayer(1)
    #p2 = RandomPlayer()
    p2 = CustomPlayerWiki(search_depth=4)
    #p2 = HumanPlayer() #Not sure what I'd need this for.
    # b = Board(p1,p2,5,5)
    # b.__board_state__ = [
    #     [0,21,0,0,0],
    #     [0,0,11,0,0],
    #     [0,0,12,0,0],
    #     [0,0,0,0,0],
    #     [0,22,0,0,0]
    # ]
    # b.__last_queen_move__["queen11"] = (1,2)
    # b.__last_queen_move__["queen21"] = (0,1)
    # b.__last_queen_move__["queen12"] = (2,2)
    # b.__last_queen_move__["queen22"] = (4,1)

    b = Board(p1,p2,4,4)
    b.__board_state__ = [
        [0,21,0,0],
        [0,0,11,0],
        [0,0,12,0],
        [0,22,0,0]
    ]
    b.__last_queen_move__["queen11"] = (1,2)
    b.__last_queen_move__["queen21"] = (0,1)
    b.__last_queen_move__["queen12"] = (2,2)
    b.__last_queen_move__["queen22"] = (3,1)


    b.move_count = 4

    output_b = b.copy()
    winner, move_history,queen_history, termination = b.play_isolation()
    print (game_as_text(winner, move_history,queen_history, termination, output_b))'''


"""Example test you can run
to make sure your AI does better
than random."""
'''piazza tips on initial placement. could use get_legal_moves
at start of alphabetaID. '''
'''board documentation - can use get_first_moves to see what's available
still. Probably want to place in center if first player first queen.'''

from isolation import Board,game_as_text
if __name__ == "__main__":
    f = CustomPlayer(search_depth=2)
    f1 = CustomPlayerOpenMove(search_depth=2)
    #CustomPlayerModAB is ready for battle.
    s = CustomPlayerMMRandom(search_depth=2)
    r = RandomPlayer()
    h = HumanPlayer()
    game = Board(f1,s,7,7)
    output_b = game.copy()
    winner, move_history,queen_history, termination = game.play_isolation()
    print (game_as_text(winner, move_history,queen_history, termination, output_b))
