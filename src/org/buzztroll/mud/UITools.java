package org.buzztroll.mud;

import java.awt.*;

public class UITools {

     /**
    * This method positions a Component in a center of a given
    * Container.
    *  
    * If the Container is null or the the Component is larger 
    * than the Container, the  Component is centered relative 
    * to the screen.
    *
    * @param Container parent - The Container relative to which
    *                           the Component is centered. 
    *
    * @param Component comp - The Component to be centered
    *
    */
    
   public static void center(Container parent, Component comp) {
      int x, y;
      Rectangle parentBounds;
      Dimension compSize = comp.getSize();

      // If Container is null or smaller than the component
      // then our bounding rectangle is the
      // whole screen
      if ((parent == null) || (parent.getBounds().width < compSize.width) ||
          (parent.getBounds().height < compSize.height)) {
         parentBounds = new Rectangle(comp.getToolkit().getScreenSize());
         parentBounds.setLocation(0,0);
      }
      // Else our bounding rectangle is the Container
      else {
          parentBounds = parent.getBounds();
      }

      // Place the component so its center is the same
      // as the center of the bounding rectangle
      x = parentBounds.x + ((parentBounds.width/2) - (compSize.width/2));
      y = parentBounds.y + ((parentBounds.height/2) - (compSize.height/2));
      comp.setLocation(x, y);
   }

}
