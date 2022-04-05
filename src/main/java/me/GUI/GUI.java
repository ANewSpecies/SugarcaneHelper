package me.GUI;

import me.Config.Config;
import me.Config.DirectionEnum;
import me.GUI.buttons.GuiBetterButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.io.IOException;

public class GUI extends GuiScreen {


    int buttonWidth = 85;
    int buttonHeight = 65;

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {



    }
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiBetterButton(0, this.width / 2 - buttonWidth / 2 , this.height / 2 - buttonHeight / 2, buttonWidth, buttonHeight, "Change direction"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawRect(0, 0, this.width, this.height, 0x30000000);
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if(button.id == 0){
            Config.Direction = DirectionEnum.values()[(Config.Direction.ordinal() + 1) % 4];
        }


    }


}
