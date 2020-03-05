/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.openvpn.ovpn3;

public class ClientAPI_TunBuilderBase {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ClientAPI_TunBuilderBase(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ClientAPI_TunBuilderBase obj) {
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
        ovpncliJNI.delete_ClientAPI_TunBuilderBase(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public boolean tun_builder_new() {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_new(swigCPtr, this);
  }

  public boolean tun_builder_set_layer(int layer) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_layer(swigCPtr, this, layer);
  }

  public boolean tun_builder_set_remote_address(String address, boolean ipv6) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_remote_address(swigCPtr, this, address, ipv6);
  }

  public boolean tun_builder_add_address(String address, int prefix_length, String gateway, boolean ipv6, boolean net30) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_address(swigCPtr, this, address, prefix_length, gateway, ipv6, net30);
  }

  public boolean tun_builder_set_route_metric_default(int metric) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_route_metric_default(swigCPtr, this, metric);
  }

  public boolean tun_builder_reroute_gw(boolean ipv4, boolean ipv6, long flags) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_reroute_gw(swigCPtr, this, ipv4, ipv6, flags);
  }

  public boolean tun_builder_add_route(String address, int prefix_length, int metric, boolean ipv6) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_route(swigCPtr, this, address, prefix_length, metric, ipv6);
  }

  public boolean tun_builder_exclude_route(String address, int prefix_length, int metric, boolean ipv6) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_exclude_route(swigCPtr, this, address, prefix_length, metric, ipv6);
  }

  public boolean tun_builder_add_dns_server(String address, boolean ipv6) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_dns_server(swigCPtr, this, address, ipv6);
  }

  public boolean tun_builder_add_search_domain(String domain) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_search_domain(swigCPtr, this, domain);
  }

  public boolean tun_builder_set_mtu(int mtu) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_mtu(swigCPtr, this, mtu);
  }

  public boolean tun_builder_set_session_name(String name) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_session_name(swigCPtr, this, name);
  }

  public boolean tun_builder_add_proxy_bypass(String bypass_host) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_proxy_bypass(swigCPtr, this, bypass_host);
  }

  public boolean tun_builder_set_proxy_auto_config_url(String url) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_auto_config_url(swigCPtr, this, url);
  }

  public boolean tun_builder_set_proxy_http(String host, int port) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_http(swigCPtr, this, host, port);
  }

  public boolean tun_builder_set_proxy_https(String host, int port) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_https(swigCPtr, this, host, port);
  }

  public boolean tun_builder_add_wins_server(String address) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_wins_server(swigCPtr, this, address);
  }

  public boolean tun_builder_set_block_ipv6(boolean block_ipv6) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_block_ipv6(swigCPtr, this, block_ipv6);
  }

  public boolean tun_builder_set_adapter_domain_suffix(String name) {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_adapter_domain_suffix(swigCPtr, this, name);
  }

  public int tun_builder_establish() {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_establish(swigCPtr, this);
  }

  public boolean tun_builder_persist() {
    return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_persist(swigCPtr, this);
  }

  public ClientAPI_StringVec tun_builder_get_local_networks(boolean ipv6) {
    return new ClientAPI_StringVec(ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_get_local_networks(swigCPtr, this, ipv6), true);
  }

  public void tun_builder_establish_lite() {
    ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_establish_lite(swigCPtr, this);
  }

  public void tun_builder_teardown(boolean disconnect) {
    ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_teardown(swigCPtr, this, disconnect);
  }

  public ClientAPI_TunBuilderBase() {
    this(ovpncliJNI.new_ClientAPI_TunBuilderBase(), true);
  }

}
