public class MatrixTable {
  private String[] headers;
  private int[][] body;
  private String[] rows;
  private boolean validOutputCheck;
  
  public MatrixTable(int[][] matrix, String[] headers, String[] rows, boolean validOutputCheck) {
    this.headers = headers;
    body = matrix;
    this.rows = rows;
    this.validOutputCheck = validOutputCheck;
  }

  public MatrixTable(int[][] matrix, String[] headers) {
    this.headers = headers;
    body = matrix;
    rows = new String[0];
    validOutputCheck = true;
  }

  public boolean valid() {
    return validOutputCheck;
  }

  public int[][] getBody() {
    return body;
  }

  public String[] getHeaders() {
    return headers;
  }

  public void setHeaders(String[] headers) {
    this.headers = headers;
  }

  public String[] getRows() {
    return rows;
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder("  ");
    
    for (int iterateHeaders = 0; iterateHeaders < headers.length; iterateHeaders++) {
      buffer.append(String.format("%3s", headers[iterateHeaders]));
      if (iterateHeaders < headers.length - 1) {
        buffer.append(" ");
      }
    }
    buffer.append("\n");

    for (int i = 0; i < body.length; i++) {
      buffer.append("|");
      
      for (int j = 0; j < body[i].length; j++) {
        buffer.append(String.format("%3s", body[i][j])).append(" ");
      }
      buffer.append("|");
      buffer.append("\n");
    }

    return buffer.toString();
  }
}