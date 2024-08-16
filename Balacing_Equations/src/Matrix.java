import java.util.Arrays;

// this Matrix class requires the Fraction class and Arrays class
public class Matrix {
  final private Fraction[][] matrix; // stores coefficients
  final private String[][] unknowns; // stores variables
  private int observedRowsPosition; // continuously tracks the assumed rank of the given matrix, until it is solved
  private int constantsPosition; // tracks column of constants / solution matrix
  private boolean isRREF = false; // determines if gaus elim has been performed
  private boolean emptyUnknowns = false; // determines if unknowns are included
  private boolean hasSolution; // determines if matrix has solutions
  private boolean hasSingleSolution; // field is not for use by accessors
  private boolean linearlyDependent; // updated by forward elim only -- not for use by accessors

  public Matrix(int[][] twoDimArray, String[] headers) {
    Fraction[][] matrixOfFractions = new Fraction[twoDimArray.length][twoDimArray[0].length];

    for (int i = 0; i < twoDimArray[0].length; i++) {
      for (int j = 0; j < twoDimArray.length; j++) {
        Fraction element = new Fraction();
        element.setNumerator(twoDimArray[j][i]);
        element.setDenominator(1);
        matrixOfFractions[j][i] = element;
      }
    }
    matrix = matrixOfFractions;
    observedRowsPosition = matrix.length - 1;
    constantsPosition = matrix[0].length - 1;

    unknowns = new String[2][headers.length + 1];
    unknowns[0] = Arrays.copyOf(headers, headers.length + 1);
    unknowns[0][unknowns[0].length - 1] = "";

    for (int i = 0; i < unknowns[0].length; i++)
      unknowns[1][i] = "";
    
    if (unknowns[0].length != matrix[0].length)
      throw new IllegalArgumentException(
          "invalid number of unknowns: only solution matrices of rank 1 or less are supported");
  }

  public Matrix(int[][] twoDimArray) {
    Fraction[][] matrixOfFractions = new Fraction[twoDimArray.length][twoDimArray[0].length];

    for (int i = 0; i < twoDimArray[0].length; i++) {
      for (int j = 0; j < twoDimArray.length; j++) {
        Fraction element = new Fraction();
        element.setNumerator(twoDimArray[j][i]);
        element.setDenominator(1);
        matrixOfFractions[j][i] = element;
      }
    }
    matrix = matrixOfFractions;
    observedRowsPosition = matrix.length - 1;
    constantsPosition = matrix[0].length - 1;

    emptyUnknowns = true;
    String[][] headers = new String[2][twoDimArray[0].length];
    
    for (int i = 0; i < headers[0].length; i++) {
      headers[0][i] = "";
      headers[1][i] = "";
    }

    unknowns = headers;
  }

  public String[][] getUnknowns() {
    return unknowns;
  }

  public Fraction[][] getMatrix() {
    return matrix;
  }

  public int getConstantsPosition() {
    return constantsPosition;
  }

  public void performGaussianElimination() {
    isRREF = true;
    hasSolution = true;
    checkForBasisVectorsZero();

    if (constantsPosition == 0) {
      for (int i = 0; i < matrix.length; i++) {
        if (matrix[i][constantsPosition].getNumerator() != 0) {
          hasSolution = false;
          break;
        }
      }

      return;
    }

    hasSingleSolution = false;

    while (!hasSingleSolution) {
      checkLinearDependency();

      forwardElim(constantsPosition); // updates hasSingleSolution
      if (!hasSolution)
        return;
    }

    backSub();
  }

  public boolean areSolutions() {
    if (!isRREF)
      this.performGaussianElimination();

    return hasSolution;
  }

  // add "singular matrix" check

  private void checkForBasisVectorsZero() {
    int currentColumn = 0;

    while (currentColumn < constantsPosition) {
      boolean columnIsEmpty = true;

      columnIsEmpty = emptyColumn(matrix, currentColumn, 0);

      if (columnIsEmpty) {
        shiftFrom(currentColumn);
        unknowns[1][unknowns[0].length - 1] = "any real number";
      } else {
        currentColumn++;
      }
    }
  }

  private void checkLinearDependency() {
    while (constantsPosition - 1 > observedRowsPosition) {
      boolean isLinearlyDependent = true;
      int checkedVectorPosition = constantsPosition - 1;
      int numBasisVectors = observedRowsPosition + 1;

      while (isLinearlyDependent) {
        if (vectorIsLinearlyDependent(checkedVectorPosition, observedRowsPosition + 1, numBasisVectors)) {
          shiftFrom(checkedVectorPosition);
          isLinearlyDependent = false;
        } else {
          checkedVectorPosition--;
          numBasisVectors--;
        }
      }
    }
  }

  private boolean vectorIsLinearlyDependent(int columnToCompare, int numRows, int maxRank) {
    Fraction[][] augmentedMatrix = new Fraction[numRows][maxRank + 1];
    int index = -1; // index increments to zero and increments maxRank + 1 times

    for (int objIndex = columnToCompare - maxRank; objIndex <= columnToCompare; objIndex++) { // get current position of
                                                                                              // vectors
      index++; // augmentedMatrix ends at a different column than columnToCompare denotes

      for (int i = 0; i < augmentedMatrix.length; i++) {
        augmentedMatrix[i][index] = matrix[i][objIndex]; // fill matrix
      }
    }

    forwardElim(augmentedMatrix, augmentedMatrix[0].length - 1);
    return linearlyDependent;
  }

  private void forwardElim(Fraction[][] array, int arrayConstantsPos) {
    int currentRow = 0;
    int currentColumn = 0;

    while ((currentRow < observedRowsPosition) && (currentColumn < arrayConstantsPos)) {
      double rowFirstElement = Math.abs(array[currentRow][currentColumn].decimal());
      int rowToSwitch = currentRow;

      for (int rowSwitchPosition = currentRow; rowSwitchPosition < observedRowsPosition + 1; rowSwitchPosition++) { // find
                                                                                                                    // greatest
                                                                                                                    // row
        double compareRowTo = Math.abs(array[rowSwitchPosition][currentColumn].decimal());

        if (rowFirstElement < compareRowTo) {
          rowFirstElement = compareRowTo;
          rowToSwitch = rowSwitchPosition;
        }
      }

      if (rowToSwitch != currentRow) { // switch current row with greatest row
        swapRows(array, currentRow, rowToSwitch);
      }

      for (int iterateRow = currentRow + 1; iterateRow < observedRowsPosition + 1; iterateRow++) { // perform forward
                                                                                                   // elim for 1st
                                                                                                   // column
        Fraction numerator = array[iterateRow][currentColumn];
        Fraction denominator = array[currentRow][currentColumn];

        Fraction factor = numerator.multiply(Fraction.negativeOne()).divide(denominator);

        elimAddition(array, factor, currentRow, iterateRow);
      }

      // move to next column
      currentRow++;
      currentColumn++;

      boolean columnIsEmpty = true;

      // check if next column(s) contain all zeros
      while ((columnIsEmpty) && (currentColumn < arrayConstantsPos)) {

        columnIsEmpty = emptyColumn(array, currentColumn, currentRow);

        if (columnIsEmpty)
          currentColumn++;
      }
    }

    // check if the output vector and the basis vectors are linearly dependent
    linearlyDependent = true;
    hasSingleSolution = true;

    if (!(currentColumn < arrayConstantsPos)) {
      hasSingleSolution = false;
      for (int iterateRow = currentRow; iterateRow < observedRowsPosition + 1; iterateRow++) {
        if (array[iterateRow][currentColumn].getNumerator() != 0) {
          linearlyDependent = false;
          break;
        }
      }
    }
  }

  // forward elim code but directly modifies observedRowsPosition, code modified
  // at linearly dependent check section
  private void forwardElim(int arrayConstantsPos) {
    int currentRow = 0;
    int currentColumn = 0;

    while ((currentRow < observedRowsPosition) && (currentColumn < arrayConstantsPos)) {
      double rowFirstElement = Math.abs(matrix[currentRow][currentColumn].decimal());
      int rowToSwitch = currentRow;

      for (int rowSwitchPosition = currentRow; rowSwitchPosition < observedRowsPosition + 1; rowSwitchPosition++) { // find
                                                                                                                    // greatest
                                                                                                                    // row
        double compareRowTo = Math.abs(matrix[rowSwitchPosition][currentColumn].decimal());

        if (rowFirstElement < compareRowTo) {
          rowFirstElement = compareRowTo;
          rowToSwitch = rowSwitchPosition;
        }
      }

      if (rowToSwitch != currentRow) { // switch current row with greatest row
        swapRows(matrix, currentRow, rowToSwitch);
      }

      for (int iterateRow = currentRow + 1; iterateRow < observedRowsPosition + 1; iterateRow++) { // perform forward
                                                                                                   // elim for 1st
                                                                                                   // column
        Fraction numerator = matrix[iterateRow][currentColumn];
        Fraction denominator = matrix[currentRow][currentColumn];

        Fraction factor = numerator.multiply(Fraction.negativeOne()).divide(denominator);

        elimAddition(matrix, factor, currentRow, iterateRow);
      }

      // move to next column
      currentRow++;
      currentColumn++;

      boolean columnIsEmpty = true;

      // check if next column(s) contain all zeros
      while ((columnIsEmpty) && (currentColumn < arrayConstantsPos)) {

        columnIsEmpty = emptyColumn(matrix, currentColumn, currentRow);

        if (columnIsEmpty) {
          currentColumn++;
        }
      }
    }

    // check if the output vector and the basis vectors are linearly dependent
    // code modified
    linearlyDependent = true;
    hasSingleSolution = true;
    int countEmptyRows = 0;

    if (!(currentColumn < arrayConstantsPos)) {
      hasSingleSolution = false;
      for (int iterateRow = currentRow; iterateRow < observedRowsPosition + 1; iterateRow++) {
        countEmptyRows++;

        if (matrix[iterateRow][currentColumn].getNumerator() != 0) {
          linearlyDependent = false;
          hasSolution = false;
        }
      }
    }

    observedRowsPosition -= countEmptyRows;
  }

  private void backSub() {
    // matrix must be a square matrix
    for (int rowsPos = observedRowsPosition; rowsPos >= 0; rowsPos--) {
      Fraction factor;
      // factor is the inverse of each diagonal element
      factor = Fraction.one().divide(matrix[rowsPos][rowsPos]);

      elimMultiplication(matrix, factor, rowsPos);
    }

    // iterate through every row in the square matrix
    for (int rowsPos = observedRowsPosition; rowsPos > 0; rowsPos--) {
      // iterate through every row above the current one (rowsPos)
      for (int iterateRows = rowsPos; iterateRows > 0; iterateRows--) {
        Fraction factor = matrix[iterateRows - 1][rowsPos];
        // iterated coefficients of current variable being substituted

        // iterate results (each coefficient of each variable in result)
        for (int iterateResult = observedRowsPosition + 1; iterateResult < matrix[0].length; iterateResult++) {
          // current row being backsub'd
          Fraction currentResult = matrix[rowsPos][iterateResult];
          // parallel row being added to
          Fraction ResultAddedTo = matrix[iterateRows - 1][iterateResult];

          // perform operation on result
          matrix[iterateRows - 1][iterateResult] = ResultAddedTo.subtract(currentResult.multiply(factor));
        }

        // remove element subtracted on left hand side
        matrix[iterateRows - 1][rowsPos] = Fraction.zero();
      }
    }
  }

  private boolean emptyColumn(Fraction[][] array, int column, int startPos) {
    boolean columnIsEmpty = true;

    // determine if column is not filled with zeros
    for (int rowSwitchPosition = startPos; rowSwitchPosition < observedRowsPosition + 1; rowSwitchPosition++) {
      int compareRowTo = array[rowSwitchPosition][column].getNumerator();

      if (compareRowTo != 0) {
        columnIsEmpty = false;
        break;
      }
    }

    return columnIsEmpty;
  }

  private void elimAddition(Fraction[][] array, Fraction factor, int rowToAdd, int rowAddedTo) {
    for (int i = 0; i < array[0].length; i++) {
      array[rowAddedTo][i] = array[rowAddedTo][i].add(array[rowToAdd][i].multiply(factor));
    }
  }

  private void elimMultiplication(Fraction[][] array, Fraction factor, int rowToScale) {
    for (int i = 0; i < array[0].length; i++) {
      array[rowToScale][i] = array[rowToScale][i].multiply(factor);
    }
  }

  private void swapRows(Fraction[][] array, int rowToSwap1, int rowToSwap2) {
    Fraction[] temp = array[rowToSwap1];

    array[rowToSwap1] = array[rowToSwap2];
    array[rowToSwap2] = temp;
  }

  private void shiftFrom(int shiftValue) {
    // shift body
    Fraction[] columnToShift = new Fraction[matrix.length];

    for (int i = 0; i < matrix.length; i++) {
      columnToShift[i] = Fraction.negativeOne().multiply(matrix[i][shiftValue]); // columnToShift is the negative of the column at shiftValue, given subtraction on both sides
    }

    for (int i = shiftValue + 1; i < matrix[0].length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        matrix[j][i - 1] = matrix[j][i];
      }
    }

    for (int i = 0; i < matrix.length; i++) {
      matrix[i][matrix[0].length - 1] = columnToShift[i]; // move columnToShift to final column
    }

    // shift headers
    String elementToShift = unknowns[0][shiftValue];

    for (int i = shiftValue + 1; i < unknowns[0].length; i++) {
      unknowns[0][i - 1] = unknowns[0][i];
    }

    unknowns[0][unknowns[0].length - 1] = elementToShift;

    constantsPosition--;
  }

  public String printMatrix() {
    StringBuilder buffer = new StringBuilder(" ");

    for (int iterateUnknowns = 0; iterateUnknowns < unknowns[0].length; iterateUnknowns++) {
      buffer.append(String.format("%5s", unknowns[0][iterateUnknowns]));
      if (iterateUnknowns < unknowns[0].length - 1) {
        buffer.append(" ");
      }
    }
    buffer.append("\n");

    for (int i = 0; i < matrix.length; i++) {
      buffer.append("|");

      for (int j = 0; j < matrix[i].length; j++) {
        buffer.append(String.format("%5s", matrix[i][j]) + " ");
      }
      buffer.append("|");
      buffer.append("\n");
    }

    // test
    buffer.append("c:").append(constantsPosition).append(" o:").append(observedRowsPosition);

    return buffer.toString();
  }

  public void formatPrintSolutions() {
    if (!isRREF)
      this.performGaussianElimination();
    if (emptyUnknowns)
      throw new IllegalArgumentException("Invalid print format: unknowns are not specified");
    
    StringBuilder buffer = new StringBuilder();
    
    for (int i = 0; i < constantsPosition; i++) { // iterate unknowns
      for (int j = constantsPosition; j < matrix[0].length; j++) { // iterate results
        // print result with associated variable
        unknowns[1][i] += matrix[i][j] + unknowns[0][j];
        if (j < matrix[0].length - 1)
          unknowns[1][i] += " + ";
      }
    }

    for (int i = 0; i < unknowns[0].length; i++) { // iterate through unknowns
      // format print unknowns
      if (i < constantsPosition)
        buffer.append(unknowns[0][i]).append(" = ").append(unknowns[1][i]).append("\n\n");
      if (i > constantsPosition)
        buffer.append(unknowns[0][i]).append(" = ").append("any real number").append("\n\n");
    }

    System.out.print(buffer);
  }
}