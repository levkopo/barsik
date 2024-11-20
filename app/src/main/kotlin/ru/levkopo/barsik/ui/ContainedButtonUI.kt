package ru.levkopo.barsik.ui

import mdlaf.animation.MaterialUIMovement
import mdlaf.components.button.MaterialButtonUI
import java.beans.ConstructorProperties
import javax.swing.*

class ContainedButton(
    text: String? = null,
    icon: Icon? = null
): JButton(text, icon) {
    class ContainedButtonUI: MaterialButtonUI() {
        override fun installUI(c: JComponent) {
            super.installUI(c)
            super.mouseHoverEnabled = false;
            super.installUI(c)
            super.mouseHoverEnabled = true;
            super.colorMouseHoverNormalButton = MaterialColors.PRIMARY
            super.background = MaterialColors.PRIMARY
            c.setBackground(super.background)

            if(super.mouseHoverEnabled){
                c.addMouseListener(
                    MaterialUIMovement.getMovement(c, this.colorMouseHoverNormalButton)
                );
            }
            //If you want use this style also for Default button
            // super.defaultBackground = MaterialColors.PURPLE_700;
            //super.colorMouseHoverDefaultButton = MaterialColors.PURPLE_500;
            super.borderEnabled = false;
        }
    }

    init {
        setUI(ContainedButtonUI())
    }
}

