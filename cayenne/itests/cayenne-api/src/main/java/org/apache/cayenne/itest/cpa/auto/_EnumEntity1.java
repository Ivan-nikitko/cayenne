package org.apache.cayenne.itest.cpa.auto;

/** Class _EnumEntity1 was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _EnumEntity1 extends org.apache.cayenne.CayenneDataObject {

    public static final String CHAR_ENUM_PROPERTY = "charEnum";
    public static final String INT_ENUM_PROPERTY = "intEnum";

    public static final String ID_PK_COLUMN = "id";

    public void setCharEnum(org.apache.cayenne.itest.cpa.Enum1 charEnum) {
        writeProperty("charEnum", charEnum);
    }
    public org.apache.cayenne.itest.cpa.Enum1 getCharEnum() {
        return (org.apache.cayenne.itest.cpa.Enum1)readProperty("charEnum");
    }
    
    
    public void setIntEnum(org.apache.cayenne.itest.cpa.Enum1 intEnum) {
        writeProperty("intEnum", intEnum);
    }
    public org.apache.cayenne.itest.cpa.Enum1 getIntEnum() {
        return (org.apache.cayenne.itest.cpa.Enum1)readProperty("intEnum");
    }
    
    
}
