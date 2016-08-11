package com.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

public abstract class PHPSerialization {
    /* generate a fast and nasty unique key for storing array sizes in hashmaps */
    protected static final String COUNT_KEY = "COUNT_KEY_" + Long.toHexString(new Random().nextLong());


    /**
     * Serializes java objects into a String PHP serialized data. Supported
     * object types include null (not really a type), Boolean, any subclass of Number
     * (e.g. Integer, Double), Character, any subclass of CharSequence (e.g. String, StringBuffer),
     * <p/>
     * Object[] (arrays) of any these listed types and subclasses of Map with keys and values
     * of any of the listed types. Primatives are implicitly supported through autoboxing
     * <p/>
     * It should be noted that this function does not provide a one-to-one mapping
     * <p/>
     * between PHP and Java types but instead makes a best effort at facilitate
     * serialized data exchanges between the two.
     *
     * @throws IllegalStateException if an attempt is made to serialize an unsupported
     *                               <p/>
     *                               object type
     */
    public static String serialize(Object obj)
            throws IllegalStateException {
        StringBuffer str = new StringBuffer();
        serialize(obj, str, new ArrayList(), false);

        return str.toString();
    }

    /**
     * This is a recursive helper function for serialize(Object) which employs
     * a StringBuffer generating the serialized output.
     * <p/>
     * Circular references are mapped to null and hence lost.
     *
     * @param obj        The java object to be serialized
     * @param str        A StringBuffer used for appending together the serialized data
     * @param refs       A List used for detecting circular references
     * @param isArrayKey A boolean value used for identifying array keys for unique treatment
     * @throws IllegalStateException if an attempt is made to serialize an unsupported
     *                               object type
     */
    private static void serialize(Object obj, StringBuffer str, List refs, boolean isArrayKey)
            throws IllegalStateException {
        boolean isCircular = false;
        // is this a circular references?

        // we only track circular references to arrays and maps
        // since these could lead to recursive loops
        for (Object o : refs) {
            if (o == obj) {
                isCircular = true;
            }
        }
        // map cicular references to null

        if (isCircular) {
            obj = null;
        }

        // map supported object types appropriately
        if (obj == null) {
            if (isArrayKey) {
                str.append("s:0:\"\";");

            } else {
                str.append("N;");
            }
        } else if (obj instanceof Boolean) {
            if (isArrayKey) {
                str.append("i:");
            } else {
                str.append("b:");

            }
            str.append(((Boolean) obj).booleanValue() ? "1;" : "0;");
        } else if (obj instanceof Number) {
            Number num = (Number) obj;
            if (isArrayKey || obj instanceof Integer || obj instanceof Byte || obj instanceof Short) {

                str.append("i:").append(num.intValue()).append(";");
            } else {
                str.append("d:").append(num.doubleValue()).append(";");
            }
        } else if (obj instanceof Character) {

            serialize(obj.toString(), str, refs, isArrayKey);
        } else if (obj instanceof CharSequence) {
            str.append("s:").append(((CharSequence) obj).length()).append(":\"").append(obj.toString()).append("\";");

        } else if (obj instanceof Object[]) {
            if (isArrayKey) {
                throw new IllegalStateException("Arrays cannot be array keys");
            }
            refs.add(obj);
            Object[] arr = (Object[]) obj;

            str.append("a:").append(arr.length).append(":{");
            for (int i = 0; i < arr.length; i++) {
                serialize(i, str, refs, true);
                serialize(arr[i], str, refs, false);
            }

            str.append("}");
        } else if (obj instanceof Map) {
            if (isArrayKey) {
                throw new IllegalStateException("Maps cannot be array keys");
            }
            refs.add(obj);

            Map map = (Map) obj;
            str.append("a:").append(map.size()).append(":{");
            for (Object key : map.keySet()) {
                serialize(key, str, refs, true);
                serialize(map.get(key), str, refs, false);

            }
            str.append("}");
        } else if (obj instanceof ArrayList) {
            if (isArrayKey) {
                throw new IllegalStateException("ArrayLists cannot be array keys");
            }
            refs.add(obj);

            ArrayList list = (ArrayList) obj;
            str.append("a:").append(list.size()).append(":{");
            for (int count = 0; count < list.size(); count++) {
                Object key = list.get(count);
                serialize(count, str, refs, true);
                serialize(key, str, refs, false);
            }
            str.append("}");
        } else {
            throw new IllegalStateException("The object type '" + obj.getClass().getName() + "' is not supported");
        }
    }


    /**
     * The unserialize class method takes a string containing a serialized php variable,
     * unserializes it and returns it as a java object mapped to the appropriate type
     *
     * @param serializedString A String of serialized text as returned by the PHP serialize() function
     * @return A java object representing the PHP variable that was in the serialized string
     * @throws IllegalStateException if an unsupported variable type was encountered in the serialized string
     */

    public static Object unserialize(String serializedString)
            throws IllegalStateException {
        Object var;
        Stack objVars = new Stack();
        StringTokenizer varTokens = new StringTokenizer(serializedString, ":");


        while (varTokens.hasMoreTokens()) {
      /* determine variable type */
            String typeToken = varTokens.nextToken(":{};");

            switch (typeToken.charAt(0)) {
                case 'a':

                    Map array = new HashMap();
        /* store the array element counts in the map */
                    array.put(COUNT_KEY, Integer.valueOf(varTokens.nextToken(":")));
        /* this must be an array */

                    objVars.push(array);
                    break;
                case 'b':
                    var = Boolean.valueOf(varTokens.nextToken(":;").equals("1"));
                    if (objVars.empty()) {
          /* this must be a standalone boolean */

                        return var;
                    } else {
          /* add this boolean to its array */
                        addVarToArray(objVars, var, 'b');
                    }
                    break;
                case 'd':
                    var = Double.valueOf(varTokens.nextToken(":;"));

                    if (objVars.empty()) {
          /* this must be a standalone double */
                        return var;
                    } else {
          /* add this double to its array */
                        addVarToArray(objVars, var, 'd');

                    }
                    break;
                case 'i':
                    var = Integer.valueOf(varTokens.nextToken(":;"));
                    if (objVars.empty()) {
          /* this must be a standalone integer */
                        return var;

                    } else {
          /* add this integer to its array */
                        addVarToArray(objVars, var, 'i');
                    }
                    break;
                case 'N':
                    var = null;
                    if (objVars.empty()) {

          /* this must be a standalone null */
                        return var;
                    } else {
          /* add this null to its array */
                        addVarToArray(objVars, var, 'N');
                    }
                    break;

      /* for now we disable PHP objects which are not yet implemented
      case 'O':
        // to-do: a simple implementation that maps PHP objects into a Java Map
        break;
      */
                case 's':

                    int len = Integer.parseInt(varTokens.nextToken(":")) + 2; /* the string length including quotes */
                    StringBuffer varStr = new StringBuffer(varTokens.nextToken(":;"));
                    while (varStr.length() < len) {

          /* append the last delimiter since it was within the string */
                        varStr.append(";");
                        varStr.append(varTokens.nextToken(";"));
                    }
                    varStr.setLength(varStr.length() - 1); /* strip trailing string quotes */

                    varStr.deleteCharAt(0); /* get rid of leading string quotes */
                    var = varStr;
                    if (objVars.empty()) {
          /* this must be a standalone string */
                        return var;
                    } else {

          /* add this string to its array */
                        addVarToArray(objVars, var, 's');
                    }
                    break;
                default:
        /* we shouldn't be here */
                    throw new IllegalStateException("Unexpected token found: " + typeToken);

            }

      /* build nested arrays or return final array */
            if (objVars.peek().getClass().equals(HashMap.class)) {
                int count = (Integer) ((Map) objVars.peek()).get(COUNT_KEY);
                if (count == 0) {

          /* get the last array stored */
                    var = objVars.pop();
          /*we no longer need the element count */
                    ((Map) var).remove(COUNT_KEY);
                    if (objVars.empty()) {
            /* this must be a standalone array */

                        return var;
                    } else {
                        addVarToArray(objVars, var, 'a');
                    }
                }
            }
        }

        return null;
    }

  /* helper method for unserialize */

    protected static void addVarToArray(Stack objVars, Object var, char varType) {
    /* if the object is a valid key type and previous object was not a key */
        if ((varType == 'i' || varType == 's') && objVars.peek().getClass().equals(HashMap.class)) {

      /* this object must be an array key
                  * push it until we get its value
                  */
            objVars.push(var);
        } else {
      /* this object must be an array value */
            Object key = objVars.pop();

            Map array = (Map) objVars.peek();
            array.put(key, var);

      /* decrement our array element count */
            int count = (Integer) array.get(COUNT_KEY);
            array.put(COUNT_KEY, new Integer(count - 1));

        }
    }
}