package org.apache.cayenne.testdo.inheritance_vertical.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.property.DateProperty;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.apache.cayenne.testdo.inheritance_vertical.IvBase;
import org.apache.cayenne.testdo.inheritance_vertical.IvOther;

/**
 * Class _IvImpl was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _IvImpl extends IvBase {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "ID";

    public static final DateProperty<Date> ATTR0 = PropertyFactory.createDate("attr0", Date.class);
    public static final StringProperty<String> ATTR1 = PropertyFactory.createString("attr1", String.class);
    public static final StringProperty<String> ATTR2 = PropertyFactory.createString("attr2", String.class);
    public static final EntityProperty<IvOther> OTHER1 = PropertyFactory.createEntity("other1", IvOther.class);
    public static final EntityProperty<IvOther> OTHER2 = PropertyFactory.createEntity("other2", IvOther.class);

    protected Date attr0;
    protected String attr1;
    protected String attr2;

    protected Object other1;
    protected Object other2;

    public void setAttr0(Date attr0) {
        beforePropertyWrite("attr0", this.attr0, attr0);
        this.attr0 = attr0;
    }

    public Date getAttr0() {
        beforePropertyRead("attr0");
        return this.attr0;
    }

    public void setAttr1(String attr1) {
        beforePropertyWrite("attr1", this.attr1, attr1);
        this.attr1 = attr1;
    }

    public String getAttr1() {
        beforePropertyRead("attr1");
        return this.attr1;
    }

    public void setAttr2(String attr2) {
        beforePropertyWrite("attr2", this.attr2, attr2);
        this.attr2 = attr2;
    }

    public String getAttr2() {
        beforePropertyRead("attr2");
        return this.attr2;
    }

    public void setOther1(IvOther other1) {
        setToOneTarget("other1", other1, true);
    }

    public IvOther getOther1() {
        return (IvOther)readProperty("other1");
    }

    public void setOther2(IvOther other2) {
        setToOneTarget("other2", other2, true);
    }

    public IvOther getOther2() {
        return (IvOther)readProperty("other2");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "attr0":
                return this.attr0;
            case "attr1":
                return this.attr1;
            case "attr2":
                return this.attr2;
            case "other1":
                return this.other1;
            case "other2":
                return this.other2;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "attr0":
                this.attr0 = (Date)val;
                break;
            case "attr1":
                this.attr1 = (String)val;
                break;
            case "attr2":
                this.attr2 = (String)val;
                break;
            case "other1":
                this.other1 = val;
                break;
            case "other2":
                this.other2 = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.attr0);
        out.writeObject(this.attr1);
        out.writeObject(this.attr2);
        out.writeObject(this.other1);
        out.writeObject(this.other2);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.attr0 = (Date)in.readObject();
        this.attr1 = (String)in.readObject();
        this.attr2 = (String)in.readObject();
        this.other1 = in.readObject();
        this.other2 = in.readObject();
    }

}
