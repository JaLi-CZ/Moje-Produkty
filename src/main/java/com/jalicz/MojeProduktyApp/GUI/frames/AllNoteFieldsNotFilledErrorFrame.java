package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.*;
import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;

import javax.swing.border.LineBorder;
import java.awt.*;

public class AllNoteFieldsNotFilledErrorFrame extends Frame {

    private static AllNoteFieldsNotFilledErrorFrame lastInstance;

    public AllNoteFieldsNotFilledErrorFrame() {
        super(new Panel(new BorderLayout()), "Chyba - všechny poznámky musí být vyplněny", Screen.ofWidth(0.4), Screen.ofHeight(0.25));

        if(lastInstance != null) lastInstance.destroy();
        lastInstance = this;

        final Text title = new Text("Všechny poznámky musí být vyplněny.");
        title.setForeground(Color.RED);
        title.setFont(Frame.getFont(1.2));

        final Text subtitle = new Text("Pokud máte přebytek poznámkových polí, klikněte na tlačítko 'Odebrat poznámku').");
        subtitle.setFont(Frame.getFont(0.8));
        subtitle.setForeground(Color.WHITE);

        final Button ok = new Button("Dobře, rozumím.");
        ok.setForeground(Color.CYAN);
        ok.setBorder(new LineBorder(Color.CYAN, 1));

        rootPanel.add(title, BorderLayout.NORTH);
        rootPanel.add(subtitle, BorderLayout.CENTER);
        rootPanel.add(ok, BorderLayout.SOUTH);

        packNormally();
        setVisible(true);

        ok.addActionListener(e -> destroy());
    }
}
