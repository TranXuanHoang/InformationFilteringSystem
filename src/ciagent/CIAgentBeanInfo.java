package ciagent;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * The <code>CIAgentBeanInfo</code> class defines the information
 * available for an agent bean.
 * 
 * @author Tran Xuan Hoang
 */
public class CIAgentBeanInfo extends SimpleBeanInfo {
	Class<CIAgent> beanClass = CIAgent.class;
	String iconColor16x16Filename;
	String iconColor32x32Filename;
	String iconMono16x16Filename;
	String iconMono32x32Filename;

	/**
	 * Creates a <code>CIAgentBeanInfo</code> object.
	 * This no-argument constructor is used within
	 * editing and activation frameworks of JavaBeans.
	 */
	public CIAgentBeanInfo() {
	}

	/**
	 * Retrieves property descriptors of an agent from bean.
	 * @return an array of property descriptors.
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor _name = new PropertyDescriptor(
					"name", beanClass, "getName", "setName");
			PropertyDescriptor _state = new PropertyDescriptor(
					"state", beanClass, "getState", "setState");

			return new PropertyDescriptor[] {_name, _state};
		} catch (IntrospectionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the icon image for the bean.
	 * @param icondKind the integer for the type of icon to be
	 * retrieved.
	 * @return the image of icon if it exists, <code>null</code>
	 * otherwise.
	 */
	@Override
	public Image getIcon(int icondKind) {
		switch (icondKind) {
		case BeanInfo.ICON_COLOR_16x16:
			return (iconColor16x16Filename != null) ?
					loadImage(iconColor16x16Filename) : null;
		case BeanInfo.ICON_COLOR_32x32:
			return (iconColor32x32Filename != null) ?
					loadImage(iconColor32x32Filename) : null;
		case BeanInfo.ICON_MONO_16x16:
			return (iconMono16x16Filename != null) ?
					loadImage(iconMono16x16Filename) : null;
		case BeanInfo.ICON_MONO_32x32:
			return (iconMono32x32Filename != null) ?
					loadImage(iconMono32x32Filename) : null;
		}

		return null;
	}

	/**
	 * Retrieves additional bean information.
	 * @return an array of <code>BeanInfo</code> objects.
	 */
	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		Class<?> superclass = beanClass.getSuperclass();

		try {
			BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
			return new BeanInfo[]{superBeanInfo};
		} catch (IntrospectionException e) {
			e.printStackTrace();
			return null;
		}
	}
} // end class CIAgentBeanInfo