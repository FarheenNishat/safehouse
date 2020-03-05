/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.openvpn.ovpn3;

public class ClientAPI_MergeConfig {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ClientAPI_MergeConfig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ClientAPI_MergeConfig obj) {
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
        ovpncliJNI.delete_ClientAPI_MergeConfig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setStatus(String value) {
    ovpncliJNI.ClientAPI_MergeConfig_status_set(swigCPtr, this, value);
  }

  public String getStatus() {
    return ovpncliJNI.ClientAPI_MergeConfig_status_get(swigCPtr, this);
  }

  public void setErrorText(String value) {
    ovpncliJNI.ClientAPI_MergeConfig_errorText_set(swigCPtr, this, value);
  }

  public String getErrorText() {
    return ovpncliJNI.ClientAPI_MergeConfig_errorText_get(swigCPtr, this);
  }

  public void setBasename(String value) {
    ovpncliJNI.ClientAPI_MergeConfig_basename_set(swigCPtr, this, value);
  }

  public String getBasename() {
    return ovpncliJNI.ClientAPI_MergeConfig_basename_get(swigCPtr, this);
  }

  public void setProfileContent(String value) {
    ovpncliJNI.ClientAPI_MergeConfig_profileContent_set(swigCPtr, this, value);
  }

  public String getProfileContent() {
    return ovpncliJNI.ClientAPI_MergeConfig_profileContent_get(swigCPtr, this);
  }

  public void setRefPathList(ClientAPI_StringVec value) {
    ovpncliJNI.ClientAPI_MergeConfig_refPathList_set(swigCPtr, this, ClientAPI_StringVec.getCPtr(value), value);
  }

  public ClientAPI_StringVec getRefPathList() {
    long cPtr = ovpncliJNI.ClientAPI_MergeConfig_refPathList_get(swigCPtr, this);
    return (cPtr == 0) ? null : new ClientAPI_StringVec(cPtr, false);
  }

  public ClientAPI_MergeConfig() {
    this(ovpncliJNI.new_ClientAPI_MergeConfig(), true);
  }

}
