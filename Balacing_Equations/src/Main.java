import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

class Main {
  public static void main(String[] args) {
    // set up window to print output to
    new GUI();
  }

  public static String solveChemEquation(String userInput) {
    MatrixTable chemEquationMatrix;
    String reactants;
    String[] individualReactants;
    MatrixTable reactantsMatrix;
    String products;
    String[] individualProducts;
    MatrixTable productsMatrix;
    String[] reducedInputYields;

    userInput = eliminateWhitespace(userInput);
    reducedInputYields = userInput.split(">", 0);

    if (reducedInputYields.length != 2) {
      return "Invalid entry\n\n";
    }

    reactants = reducedInputYields[0];
    products = reducedInputYields[1];

    individualReactants = reactants.split("\\+", 0);
    individualProducts = products.split("\\+", 0);
    
    reactantsMatrix = ArrayOperations.arrayToMatrixOfSubscripts(individualReactants);
    productsMatrix = ArrayOperations.arrayToMatrixOfSubscripts(individualProducts);

    if (!reactantsMatrix.valid() || !productsMatrix.valid()) {
      return "Invalid entry\n\n";
    }

    if (!(Arrays.equals(reactantsMatrix.getRows(), productsMatrix.getRows()))) {
      return "Invalid entry\n\n";
    }

    chemEquationMatrix = augmentMatrices(reactantsMatrix, productsMatrix);
    String[] newHeaders = chemEquationMatrix.getHeaders();
    
    for (int i = 0; i < newHeaders.length; i++) {
      newHeaders[i] = newHeaders[i].substring(1, newHeaders[i].length() - 1);
    }

    chemEquationMatrix.setHeaders(newHeaders);

    Matrix chemEquation = new Matrix(chemEquationMatrix.getBody(), chemEquationMatrix.getHeaders());
    chemEquation.performGaussianElimination();
    
    return printSolutionsScalarsOne(chemEquation);
  }

  public static MatrixTable augmentMatrices(MatrixTable reactants, MatrixTable products) {
    int[][] reactantArray = reactants.getBody();
    int[][] productArray = products.getBody();
    String[] unknowns;
    int[][] augmentedMatrix;
    int currentIndex;
    MatrixTable chemEquationMatrix;

    unknowns = concatStringArrays(reactants.getHeaders(), products.getHeaders());
    
    augmentedMatrix = new int[reactantArray.length][reactantArray[0].length + productArray[0].length + 1];

    for (currentIndex = 0; currentIndex < reactantArray[0].length; currentIndex++) { // fill with reactants
      for (int j = 0; j < augmentedMatrix.length; j++) {
        augmentedMatrix[j][currentIndex] = reactantArray[j][currentIndex];
      }
    }

    for (int i = 0; i < productArray[0].length; i++) { // fill with products and move to other side of equation
      for (int j = 0; j < augmentedMatrix.length; j++) {
        augmentedMatrix[j][currentIndex + i] = -1 * productArray[j][i];
      }
    }

    chemEquationMatrix = new MatrixTable(augmentedMatrix, unknowns);
    return chemEquationMatrix;
  }

  public static String printSolutionsScalarsOne(Matrix chemMatrix) {
    Fraction[][] matrix = chemMatrix.getMatrix();
    String[][] unknowns = chemMatrix.getUnknowns();
    int constantsPosition = chemMatrix.getConstantsPosition();
    StringBuilder buffer = new StringBuilder();
    // store each unknown's value
    Fraction[] solutions = new Fraction[unknowns[0].length];
    
    for (int i = 0; i < constantsPosition; i++) { // iterate unknowns
      Fraction sum = Fraction.zero();
      
      for (int j = constantsPosition; j < matrix[0].length; j++) { // iterate results
        // print result with associated variable
        sum = sum.add(matrix[i][j]);
      }
    // store result
    solutions[i] = sum;
    }
    // set variable unknowns to 1
    for (int i = constantsPosition + 1; i < solutions.length; i++) {
      solutions[i] = Fraction.one();
    }
    // get LCD
    int elementLCD = solutions[0].getDenominator();

    for (int i = 1; i < constantsPosition; i++) {
      elementLCD = lcd(elementLCD, solutions[i].getDenominator());
    }

    Fraction finalElementLCD = new Fraction();
    finalElementLCD.setNumerator(elementLCD);
    finalElementLCD.setDenominator(1);
    // scale every solution by LCD
    for (int i = 0; i < solutions.length; i++) {
      if (i != constantsPosition)
        solutions[i] = solutions[i].multiply(finalElementLCD);
    }
    // print out solutions
    for (int i = 0; i < unknowns[0].length; i++) {
      if (i != constantsPosition) {
        buffer.append( unknowns[0][i] ).append(" = ").append( solutions[i].getNumerator() ).append("\n\n");
      }
    }

    return buffer.toString();
  }
  
  public static String eliminateWhitespace(String userInput) {
    boolean hasSpace = userInput.contains(" ");

    while (hasSpace) {
      userInput = userInput.replaceAll(" ", "");

      hasSpace = userInput.contains(" ");
    }

    return userInput;
  }

  public static String[] concatStringArrays(String[] array1, String[] array2) {
    String[] mergedArray = new String[array1.length + array2.length];
    int currentIndex;

    for (currentIndex = 0; currentIndex < array1.length; currentIndex++) {
      mergedArray[currentIndex] = array1[currentIndex];
    }
    for (; currentIndex < mergedArray.length; currentIndex++) {
      mergedArray[currentIndex] = array2[currentIndex - array1.length];
    }

    return mergedArray;
  }

  public static int lcd(int denom1, int denom2) {
    int factor = denom1;
    while ((denom1 % denom2) != 0)
      denom1 += factor;
    return denom1;
  }
}

class ArrayOperations {

  public static MatrixTable arrayToMatrixOfSubscripts(String[] array) {
    String[] elements = {""};
    String[] unknowns = {""};
    int[][] subscriptMatrix;
    int[][] invalidMatrix = {{}};
    MatrixTable invalidEntry = new MatrixTable(invalidMatrix, unknowns, elements, false);

    if (!(bracketsValid(array))) {
      return invalidEntry;
    }
    // a valid entry has an even number of brackets

    if (!(subscriptsValid(array))) {
      return invalidEntry;
    }
    // even num subscripts

    if (!(colonsValid(array))) {
      return invalidEntry;
    }
    // even num colons

    elements = findElements(array);
    if (elements[0].compareTo("-1") == 0) {
      return invalidEntry;
    }

    alphabeticSort(elements);

    unknowns = findUnknowns(array);
    if (unknowns[0].compareTo("-1") == 0) {
      return invalidEntry;
    }

    subscriptMatrix = new int[elements.length][unknowns.length];

    for (int i = 0; i < unknowns.length; i++) { // iterate through unknowns

      for (int j = 0; j < elements.length; j++) { // iterate through chemical elements
        int length = elements[j].length();

        for (String index : array) { // iterate through array elements
          if (!index.contains(unknowns[i])) {
            continue;
          }
          int k = 0;

          while (index.indexOf(elements[j], k) > -1) { // search for subscripts
            int subscriptPos = index.indexOf(elements[j], k) + length;

            if (subscriptPos < index.length()) { // validate subscript location
              if (index.charAt(subscriptPos) == '[') { // check if subscript is 1
                subscriptMatrix[j][i] += 1;
                k = subscriptPos;
                continue;
              }
              if (index.indexOf("_", subscriptPos + 1) != -1) { // check subscripts not equal to 1
                int subscriptValue;
                int secondSubscriptPos = index.indexOf("_", subscriptPos + 1);
                String subscript = index.substring(subscriptPos + 1, secondSubscriptPos);
                Scanner checkInt = new Scanner(subscript);

                if (checkInt.hasNextInt()) {
                  subscriptValue = checkInt.nextInt();
                  checkInt.close();
                } else {
                  checkInt.close();
                  return invalidEntry;
                }

                if (subscript.compareTo(String.valueOf(subscriptValue)) == 0) {
                  subscriptMatrix[j][i] += subscriptValue;
                } else {
                  return invalidEntry;
                }
              } else {
                return invalidEntry;
              }
            } else {
              subscriptMatrix[j][i] += 1;
            }
            k = subscriptPos;
          }
        }
      }
    }

    return new MatrixTable(subscriptMatrix, unknowns, elements, true);
  }

  private static String[] findElements(String[] array) {
    ArrayList<String> elements = new ArrayList<>(); // once elements are identified, array length is constant, and ArrayList will be converted to array
    String[] returnElements;
    String[] invalidEntry = { "-1" }; // works as invalid entry since element must contain brackets
    String currentElement;
    int openBracketPosition;
    int closeBracketPosition;
    boolean newElement;

    for (String index : array) {
      int currentPos = index.indexOf("[");

      while (index.indexOf("[", currentPos) > -1) {
        newElement = true;

        openBracketPosition = index.indexOf("[", currentPos);
        closeBracketPosition = index.indexOf("]", currentPos);
        currentElement = index.substring(openBracketPosition, closeBracketPosition + 1);

        if ((currentElement.equals("[]")) || ((currentElement.contains(":")))
            || ((currentElement.contains("_")))) {
          return invalidEntry;
        }

        for (String existingElement : elements) {
          if (existingElement != null) {
            if (currentElement.equals(existingElement)) {
              newElement = false;
            }
          }
        }

        if (newElement)
          elements.add(currentElement);

        currentPos = closeBracketPosition + 1;
      }

    }

    returnElements = new String[elements.size()];
    returnElements = elements.toArray(returnElements);

    return returnElements;
  }

  private static String[] findUnknowns(String[] array) {
    String[] unknowns = new String[array.length];
    String[] invalidEntry = { "-1" }; // works as invalid entry since unknown must contain colons
    String currentUnknown;
    int lowerColonPos;
    int upperColonPos;

    for (int i = 0; i < array.length; i++) {
      lowerColonPos = array[i].indexOf(":");
      upperColonPos = array[i].indexOf(":", lowerColonPos + 1);
      currentUnknown = array[i].substring(lowerColonPos, upperColonPos + 1);

      if (currentUnknown.equals("::")) {
        return invalidEntry;
      }

      for (String anyUnknown : unknowns) {
        if (anyUnknown != null) {
          if (currentUnknown.equals(anyUnknown)) {
            return invalidEntry;
          }
        }
      }

      unknowns[i] = currentUnknown;
    }

    return unknowns;
  }

  private static boolean bracketsValid(String[] array) {
    int countOccurrenceOpenBracket;
    int countOccurrenceCloseBracket;

    for (String index : array) {
      countOccurrenceOpenBracket = 0;
      countOccurrenceCloseBracket = 0;
      {
        int currentPos = 0;

        while (index.indexOf("[", currentPos) > -1) {
          countOccurrenceOpenBracket += 1;
          currentPos = index.indexOf("[", currentPos) + 1;
        }
      }

      {
        int currentPos = 0;

        while (index.indexOf("]", currentPos) > -1) {
          countOccurrenceCloseBracket += 1;
          currentPos = index.indexOf("]", currentPos) + 1;

          if (!(currentPos >= index.length())) {
            if (index.charAt(currentPos) != '[' && index.charAt(currentPos) != '_') {
              return false;
            }
          }
        }
      }
      if ((countOccurrenceOpenBracket != countOccurrenceCloseBracket) || (countOccurrenceOpenBracket == 0)) {
        return false;
      }
    }

    return true;
  }

  private static boolean subscriptsValid(String[] array) {
    for (String index : array) {
      int currentPos = 0;
      int subscriptCount = 0;

      while (index.indexOf("_", currentPos) != -1) {
        subscriptCount += 1;
        currentPos = index.indexOf("_", currentPos) + 1;
      }

      if ((subscriptCount % 2 != 0)) {
        return false;
      }
    }

    return true;
  }

  private static boolean colonsValid(String[] array) {
    for (String index : array) {
      int currentPos = 0;
      int colonCount = 0;

      while (index.indexOf(":", currentPos) != -1) {
        colonCount += 1;
        currentPos = index.indexOf(":", currentPos) + 1;
      }

      if ((colonCount % 2 != 0) || (colonCount / 2 != 1)) {
        return false;
      }
    }

    return true;
  }

  private static void alphabeticSort(String[] array) {
    int indexSmallest;

    for (int i = 0; i < array.length - 1; i++) {

      indexSmallest = i;
      for (int j = indexSmallest + 1; j < array.length; j++) {
        if (array[j].compareTo(array[indexSmallest]) < 0) {
          indexSmallest = j;
        }
      }

      String swap = array[i];
      array[i] = array[indexSmallest];
      array[indexSmallest] = swap;
    }
  }

}