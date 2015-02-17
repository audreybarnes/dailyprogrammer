package amb.AsteroidCheck

class SpaceProbe {
    def rand = new Random()
    def gridSize
    def width = 50
    List <PositionData > squareList
    def asteroidsPercentage = 0.3
    def gravityWellPercentage = 0.011
    def adjacentDeltas = [1, 1+width, 1-width, width, width-1, -1, -1-width, -width]
    def gpossible
    def apossible
    def lastSquare
    def length

    public SpaceProbe(){
       gridSize = width*width
       lastSquare=gridSize-1
       squareList=new PositionData[gridSize+1]
        (1..gridSize).each { i ->
           squareList[i]= new PositionData()}
       apossible = 2..lastSquare as Set
        // if the gravity well is on these positions, you can't get to start and/or end
       gpossible= 3..gridSize-2 as Set
       gpossible.removeAll(width,width+1,gridSize-width,gridSize-width-1)
    }

    public static void main(String[] Args) {
        SpaceProbe sp = new SpaceProbe()
        sp.exec()
    }

    def exec() {
        createGrid()
        def message = findPath()? "Path is $length steps long" : "No path found"
        drawSpace()
        println(message)
    }
    /**
     * Since we have a one dimensional array, set which squares are edges and therefore not next to each other
     * Set start and end point, then add gravity wells and asteroids.
     * @return
     */
    def createGrid() {
       //set edges for first square
       squareList[1].setEdges(0,width)
       width.step(gridSize-width+1,width){ i ->
          squareList[i].setEdges(i+1,width)
          squareList[i+1].setEdges(i,width)
       }
        squareList[1].id='S'
        squareList[gridSize].id='E'
        populateGravity()
        populateAsteroids()
    }
    /**
     * Everything else is setup/print out.  This is the guts of the code.
     * Start at square 1, find all open squares next to it and claim them for square 1, adding these squares to the list
     * of squares to check next.  Then start over with the list of squares you just made, checking each for availability
     * and claiming each unvisited one for the square that got there first.  Make a new list of 'squares to check next'
     * and abandon the old one once you have walked through all of them.  Lather, rinse, repeat until someone hits the
     * last square (or there are no paths forward).  Print out 'no solution' or the final grid.
     * @return success/failure
     */
    def findPath() {
        def nextChecks = []
        def currentSquares = [1]
        def currentPos=1;
        boolean success=false;
        int count=0
        while (currentSquares.size()>0 && !success) {
            count=0
            while (count < currentSquares.size() && !success) {
                currentPos = currentSquares[count]
                (0..7).each {
                    if (!success) {
                        def nextPos = adjacentDeltas[it] + currentPos
                        if (nextPos == gridSize && !squareList[currentPos].edge.contains(nextPos)) {
                            // we are done!
                            drawSuccess(currentPos)
                            success = true;
                        } else if (validSquare(currentPos,nextPos) && squareList[nextPos].goodMove(currentPos)) {
                            nextChecks += nextPos
                        }
                    }
                }
                count++
            }
            currentSquares=nextChecks
            nextChecks = []
        }
        success
    }
    /**
     * Set all the squares on the successful path to the 'U' by following the parent trail
     * @param pathPos - current square
     */
    def drawSuccess(pathPos) {
       length = 1
       while (pathPos>1) {
           squareList[pathPos].id='U'
           pathPos = squareList[pathPos].parent
           length++
       }

    }
    /**
     * Pick a random square from the set of possible squares.  Remove it and the surrounding squares from consideration.
     * Center square is 'G' and surrounding squares are 'X', unless 'X' is already taken
     * @return
     */
    def populateGravity(){
        def gravityWellCount = numItems(gravityWellPercentage)
        (0 ..< gravityWellCount).each {
            def ssize=gpossible.size()
            int pPos=rand.nextInt(ssize)-1
            int gPos = gpossible[pPos]
            squareList[gPos].id='G'
            gpossible.remove(gPos)
            apossible.remove(gPos)
            (0..7).each {
                def xpos=adjacentDeltas[it]+gPos
                if (validSquare(gPos, xpos) && squareList[xpos].id=='.'){
                    squareList[xpos].id = 'X'
                    apossible.remove(xpos)
                }
            }

        }
    }
    /**
     * Are we on the grid and next to the gravity well?
     * @param gPos - position of the gravity well
     * @param xpos - potential square next to said gPos
     * @return
     */
    def validSquare(gPos, xpos){
      xpos>1 && xpos<gridSize && !squareList[gPos].edge.contains(xpos)
    }
    /**
     * Pick a random square in the available squares and drop and 'A' in it.  Square is removed from list.
     * @return
     */
    def populateAsteroids(){
        def asteroidCount = numItems(asteroidsPercentage)
        (0 ..< asteroidCount).each {
            int pPos=rand.nextInt(apossible.size())-1
            int aPos=apossible[pPos]
            squareList[aPos].id='A'
            apossible.remove(aPos)
        }
    }

    def numItems(percentage) {
        def count = Math.floor( gridSize * percentage) as Integer
        count>0 ? count : 1
    }
    /**
     * Just printing out the grid
     * @return
     */
    def drawSpace() {
        def pos = 1
        (0 ..< width).each {
            (0 ..< width).each {
                print squareList[pos++].id
            }
            println ''
        }
    }
}