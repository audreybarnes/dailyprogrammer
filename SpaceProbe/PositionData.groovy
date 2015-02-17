package amb.AsteroidCheck

/**
 * Created by ap869 on 12/15/14.
 */
class PositionData {
   Set edge = []
   def parent=0
   def id = "."

    /**
     * These squares are over edges, so not actually next to the square
     * @param bad - the point over the edge
     * @param width - indicates the square 'above and below' bad that are also edges
     * @return
     */
   def setEdges(bad,width) {
       edge=[bad-width,bad,bad+width]
   }
    /**
     * If this is a vaild square and no one has visited this square before, claim it!
     * @param previousPosition - the official previous square in this path
     * @return if this is a good move
     */
   def goodMove(previousPosition){
       if (id=='.' && parent==0 ) {
           parent = previousPosition
           true
       } else false;
   }
}
