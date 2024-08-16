public class Paragraph {
  private String text;
  private int lineLength;

  public Paragraph(String inputText) {
    text = inputText;
    lineLength = -1;
  }

  public void paragraphLineFormat() {
    paragraphLineFormat(70);
  }

  public void paragraphLineFormat(int charsPerLine) {
    setLineLength(charsPerLine);
    StringBuilder formattedText = new StringBuilder();
    int index = 0;
    String[] paragraphs;
    boolean endsWithNewline = false;

    if (text.charAt(text.length() - 1) == '\n') {
      text += "n"; // forcing newlines after string to join split
      endsWithNewline = true;
    }

    paragraphs = text.split("\n", 0);

    if (endsWithNewline) {
      paragraphs[paragraphs.length - 1] = "";
    }

    for (String item : paragraphs) {
      int previousPosition = 0;
      int positionException;
      boolean lineFull;

      for (int currentIndex = lineLength; currentIndex < item.length(); currentIndex += lineLength) {
        lineFull = false;
        String currentLine = item.substring(previousPosition, currentIndex);
        positionException = currentLine.lastIndexOf(" ");

        if (positionException == -1) {
          positionException = currentIndex;
          lineFull = true;
        } else {
          positionException += previousPosition;
        }

        formattedText.append(item, previousPosition, positionException);
        if (!((item.charAt(item.length() - 1) == ' ') && (positionException == item.length() - 1)))
          formattedText.append("\n");

        if (!lineFull)
          currentIndex = positionException + 1;

        previousPosition = currentIndex;
      }

      formattedText.append(item.substring(previousPosition));

      if (index != paragraphs.length - 1) {
        formattedText.append("\n");
      }
      index++;
    }

    text = formattedText.toString();
  }

  public String toString() {
    return text;
  }

  public void setText(String inputText) {
    text = inputText;
  }

  public int getLineLength() {
    return lineLength;
  }

  private void setLineLength(int charsPerLine) {
    lineLength = charsPerLine;
    if (lineLength <= 0)
      throw new IllegalArgumentException("invalid number of characters per line");
  }

}