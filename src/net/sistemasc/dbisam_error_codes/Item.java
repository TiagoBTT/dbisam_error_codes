package net.sistemasc.dbisam_error_codes;

/**
 * Simple class to store every field
 *
 */
public class Item {
  private int errId;
  private String errConst;
  private String errMsg;
  private String errDesc;
  public int getErrId() {
    return errId;
  }
  public void setErrId(int errId) {
    this.errId = errId;
  }
  public String getErrConst() {
    return errConst;
  }
  public void setErrConst(String errConst) {
    this.errConst = errConst;
  }
  public String getErrMsg() {
    return errMsg;
  }
  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
  public String getErrDesc() {
    return errDesc;
  }
  public void setErrDesc(String errDesc) {
    this.errDesc = errDesc;
  }
  /* Returns Id and const as a short description
   */
  @Override
  public String toString() {
    return errId + " " + errConst;
  }
  public Item(int errId, String errConst, String errMsg, String errDesc) {
    super();
    this.errId = errId;
    this.errConst = errConst;
    this.errMsg = errMsg;
    this.errDesc = errDesc;
  }
}
