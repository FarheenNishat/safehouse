/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.openvpn.ovpn3;

public class ClientAPI_LogInfo {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ClientAPI_LogInfo(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ClientAPI_LogInfo obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        ovpncliJNI.delete_ClientAPI_LogInfo(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ClientAPI_LogInfo() {
    this(ovpncliJNI.new_ClientAPI_LogInfo__SWIG_0(), true);
  }

  public ClientAPI_LogInfo(String str) {
    this(ovpncliJNI.new_ClientAPI_LogInfo__SWIG_1(str), true);
  }

  public void setText(String value) {
    ovpncliJNI.ClientAPI_LogInfo_text_set(swigCPtr, this, value);
  }

  public String getText() {
    return ovpncliJNI.ClientAPI_LogInfo_text_get(swigCPtr, this);
  }

}
