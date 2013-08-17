package com.sun.corba.se.ActivationIDL;


/**
* com/sun/corba/se/ActivationIDL/EndPointInfoHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../../../src/share/classes/com/sun/corba/se/ActivationIDL/activation.idl
* Wednesday, January 30, 2002 8:23:27 AM PST
*/


// passing a struct containing endpointType and port-#s
abstract public class EndPointInfoHelper
{
  private static String  _id = "IDL:ActivationIDL/EndPointInfo:1.0";

  public static void insert (org.omg.CORBA.Any a, com.sun.corba.se.ActivationIDL.EndPointInfo that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static com.sun.corba.se.ActivationIDL.EndPointInfo extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [2];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "endpointType",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.ActivationIDL.TCPPortHelper.id (), "TCPPort", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "port",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (com.sun.corba.se.ActivationIDL.EndPointInfoHelper.id (), "EndPointInfo", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static com.sun.corba.se.ActivationIDL.EndPointInfo read (org.omg.CORBA.portable.InputStream istream)
  {
    com.sun.corba.se.ActivationIDL.EndPointInfo value = new com.sun.corba.se.ActivationIDL.EndPointInfo ();
    value.endpointType = istream.read_string ();
    value.port = istream.read_long ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.corba.se.ActivationIDL.EndPointInfo value)
  {
    ostream.write_string (value.endpointType);
    ostream.write_long (value.port);
  }

}
