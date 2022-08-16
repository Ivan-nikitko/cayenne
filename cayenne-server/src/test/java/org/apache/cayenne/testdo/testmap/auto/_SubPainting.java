package org.apache.cayenne.testdo.testmap.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.NumericIdProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.apache.cayenne.testdo.testmap.SubPainting;

/**
 * Class _SubPainting was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SubPainting extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final EntityProperty<SubPainting> SELF = PropertyFactory.createSelf(SubPainting.class);
    public static final NumericIdProperty<Integer> PAINTING_ID_PK_PROPERTY = PropertyFactory.createNumericId("PAINTING_ID", "SubPainting", Integer.class);
    public static final String PAINTING_ID_PK_COLUMN = "PAINTING_ID";

    public static final StringProperty<String> PAINTING_TITLE = PropertyFactory.createString("paintingTitle", String.class);

    protected String paintingTitle;


    public void setPaintingTitle(String paintingTitle) {
        beforePropertyWrite("paintingTitle", this.paintingTitle, paintingTitle);
        this.paintingTitle = paintingTitle;
    }

    public String getPaintingTitle() {
        beforePropertyRead("paintingTitle");
        return this.paintingTitle;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "paintingTitle":
                return this.paintingTitle;
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
            case "paintingTitle":
                this.paintingTitle = (String)val;
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
        out.writeObject(this.paintingTitle);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.paintingTitle = (String)in.readObject();
    }

}
