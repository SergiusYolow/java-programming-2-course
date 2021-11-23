import java.beans.*;
import java.awt.*;

/**
 * This BeanInfo class provides additional information about the YesNoPanel
 * bean in addition to what can be obtained through  introspection alone.
 **/
public class MyElementBeanInfo extends SimpleBeanInfo
{
    /**
     * Return an icon for the bean.  We should really check the kind argument
     * to see what size icon the beanbox wants, but since we only have one
     * icon to offer, we just return it and let the beanbox deal with it
     **/
    public Image getIcon(int kind)
    {
        return loadImage("YesNoPanelIcon.gif");
    }

    /**
     * This is a convenience method for creating PropertyDescriptor objects
     */
    static PropertyDescriptor prop(String name, String description)
    {
        try
        {
            PropertyDescriptor p =
                    new PropertyDescriptor(name, MyElement.class);
            p.setShortDescription(description);
            return p;
        } catch (IntrospectionException e)
        {
            return null;
        }
    }

    // Initialize a static array of PropertyDescriptor objects that provide
    // additional information about the properties supported by the bean.
    // By explicitly specifying property descriptors, we are able to provide
    // simple help strings for each property; these would not be available to
    // the beanbox through simple introspection.  We are also able to register
    // a special property editors for the messageText property
    static PropertyDescriptor[] props = {
            prop("StaticText", "StaticText"),
            prop("list1", "list1"),
            prop("list2", "list2"),
            prop("commandKey", "commandKey"),
            prop("font", "The font for the message and buttons")
    };

    /**
     * Return the property descriptors for this bean
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return props;
    }

    /**
     * The message property is most often customized; make it the default
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
