package me;

import me.Config.Config;
import me.Config.DirectionEnum;
import me.GUI.GUI;
import me.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = SugarcaneHelper.MODID, version = SugarcaneHelper.VERSION)
public class SugarcaneHelper
{
    public static final String MODID = "scmath";
    public static final String VERSION = "1.0";


    /*
     ** @author JellyLab
     *  @contributor XHacer
     */
    Minecraft mc = Minecraft.getMinecraft();

    public static boolean enabled = false;


    boolean locked = false;
    boolean process1 = false;
    boolean process2 = false;
    boolean process3 = false;
    boolean process4 = false;
    boolean error = false;
    boolean emergency = false;
    boolean setspawned = false;
    boolean setAntiStuck = false;
    boolean set = false; //whether HAS CHANGED motion (1&2)


    double beforeX = 0;
    double beforeZ = 0;
    double deltaX = 10000;
    double deltaZ = 10000;
    double initialX = 0;
    double initialZ = 0;



    boolean notInIsland = false;
    boolean shdBePressingKey = true;

    public int keybindA = mc.gameSettings.keyBindLeft.getKeyCode();
    public int keybindD = mc.gameSettings.keyBindRight.getKeyCode();
    public int keybindW = mc.gameSettings.keyBindForward.getKeyCode();
    public int keybindS = mc.gameSettings.keyBindBack.getKeyCode();
    public int keybindAttack = mc.gameSettings.keyBindAttack.getKeyCode();

    static KeyBinding[] customKeyBinds = new KeyBinding[2];

    static volatile int totalMnw = 0;
    static volatile int totalEnw = 0;
    static volatile int totalMoney = 0;
    static volatile int prevMoney = -999;
    int mode = 0;
    static volatile int moneyper10sec = 0;

    MouseHelper mouseHelper = new MouseHelper();
    int playerYaw = 0;





    private static Logger logger;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ScheduleRunnable(checkPriceChange, 1, TimeUnit.SECONDS);
        customKeyBinds[0] = new KeyBinding("Open GUI", Keyboard.KEY_RSHIFT, "SugarcaneHelper");
        customKeyBinds[1] = new KeyBinding("Toggle script", Keyboard.KEY_GRAVE, "SugarcaneHelper");
        ClientRegistry.registerKeyBinding(customKeyBinds[0]);
        ClientRegistry.registerKeyBinding(customKeyBinds[1]);
    }

    @SubscribeEvent
    public void onOpenGui(final GuiOpenEvent event) {
        if (event.gui instanceof GuiDisconnected) {
            enabled = false;
        }
    }


    @SubscribeEvent
    public void onMessageReceived(ClientChatReceivedEvent event){

        if(event.message.getFormattedText().contains("You were spawned in Limbo") && !notInIsland && enabled) {
            activateFailsafe();
            ScheduledExecutorService executor1 = Executors.newScheduledThreadPool(1);
            executor1.schedule(LeaveSBIsand, 8, TimeUnit.SECONDS);

        }
        if((event.message.getFormattedText().contains("Sending to server") && !notInIsland && enabled)){
            activateFailsafe();
            ScheduledExecutorService executor1 = Executors.newScheduledThreadPool(1);
            executor1.schedule(WarpHome, 10, TimeUnit.SECONDS);
        }
        if((event.message.getFormattedText().contains("DYNAMIC") && notInIsland)){
            error = true;
        }
        if((event.message.getFormattedText().contains("SkyBlock Lobby") && !notInIsland && enabled)){
            activateFailsafe();
            ScheduledExecutorService executor1 = Executors.newScheduledThreadPool(1);
            executor1.schedule(LeaveSBIsand, 10, TimeUnit.SECONDS);

        }


    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent event)
    {

        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {

            mc.fontRendererObj.drawStringWithShadow(Config.Direction.toString(), 5, 5, -1);
           /* if(Config.CropType.equals(CropEnum.NETHERWART) && Config.inventoryPriceCalculator) {
                mc.getTextureManager().bindTexture(mutantNetherwartImage);
                GuiScreen.drawModalRectWithCustomSizedTexture(4, 50, 1, 1, 12, 12, 12, 12);
                mc.getTextureManager().bindTexture(enchantedNetherwartImage);
                GuiScreen.drawModalRectWithCustomSizedTexture(4, 62, 1, 1, 12, 12, 12, 12);
                Utils.drawString("x" + totalMnw, 20, 50, 0.8f, -1);
                Utils.drawString("x" + totalEnw, 20, 62, 0.8f, -1);
                Utils.drawString("$" + totalMoney, 6, 78, 0.95f, -1);
            }
            if(Config.CropType.equals(CropEnum.NETHERWART) && Config.profitCalculator) {
                Utils.drawString("profit/min = " + moneyper10sec * 6, 6, 94, 0.8f, -1);
                Utils.drawString("profit/h = " + moneyper10sec * 6 * 60, 6, 104, 0.8f, -1);
            }*/

        }

    }



    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnTickPlayer(TickEvent.ClientTickEvent event) { //Client -> player

        if (event.phase != TickEvent.Phase.START) return;

        // profit calculator && angle caculation
        if( mc.thePlayer != null && mc.theWorld != null){

            /*int tempEnw = 0; int tempMnw = 0;
            for (int i = 0; i < 35; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if(stack != null) {
                    if (stack.getDisplayName().contains("Enchanted Nether Wart"))//
                        tempEnw = tempEnw + stack.stackSize;

                    if (stack.getDisplayName().contains("Mutant Nether Wart"))
                        tempMnw = tempMnw + stack.stackSize;
                }

            }
            totalMnw = tempMnw; totalEnw = tempEnw;
            totalMoney = totalMnw * 51504 + totalEnw * 320;*/

        }

        //script code
        if (enabled && mc.thePlayer != null && mc.theWorld != null) {

            //always
            mc.gameSettings.pauseOnLostFocus = false;
            mc.thePlayer.inventory.currentItem = 0;
            mc.gameSettings.gammaSetting = 100;
            if(!emergency) {
                KeyBinding.setKeyBindState(keybindW, false);
                KeyBinding.setKeyBindState(keybindS, false);
            }
            if (!shdBePressingKey) {
                KeyBinding.setKeyBindState(keybindA, false);
                KeyBinding.setKeyBindState(keybindD, false);
            }
            //angles (locked)
            if(!emergency && !notInIsland) {
                mc.thePlayer.rotationPitch = 0;
                Utils.hardRotate(playerYaw);

            }
            //INITIALIZE
            if (!locked) {
                KeyBinding.setKeyBindState(keybindA, false);
                KeyBinding.setKeyBindState(keybindD, false);
                locked = true;
                initialize();
                ScheduleRunnable(checkChange, 3, TimeUnit.SECONDS);
            }
            //antistuck
            if(!setAntiStuck){
                if (playerBlock() == Blocks.farmland || playerBlock() == Blocks.soul_sand){

                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                            "[Sugarcane Helper] : " + EnumChatFormatting.DARK_GREEN + "Detected stuck"));

                    setAntiStuck = true;
                    process4 = true;
                    ScheduleRunnable(stopAntistuck, 800, TimeUnit.MILLISECONDS);
                }

            }
            if(deltaX < 0.8d && deltaZ < 0.8d && !notInIsland && !emergency && !setAntiStuck){

                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                        "[Sugarcane Helper] : " + EnumChatFormatting.DARK_GREEN + "Detected stuck"));
                setAntiStuck = true;
                process4 = true;
                ScheduleRunnable(stopAntistuck, 800, TimeUnit.MILLISECONDS);

            }

            //bedrock failsafe
            Block blockStandingOn = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock();
            if(blockStandingOn == Blocks.bedrock && !emergency) {
                KeyBinding.setKeyBindState(keybindAttack, false);
                process1 = false;
                process2 = false;
                process3 = false;
                process4 = false;
                ScheduleRunnable(EMERGENCY, 200, TimeUnit.MILLISECONDS);
                emergency = true;

            }

            //change motion
            Block blockIn = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();

            double dx = Math.abs(mc.thePlayer.posX - mc.thePlayer.lastTickPosX);
            double dz = Math.abs(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
            double dy = Math.abs(mc.thePlayer.posY - mc.thePlayer.lastTickPosY);
            boolean falling = blockIn == Blocks.air && dy != 0;
            if ((float)dx == 0 && dz == 0 && !notInIsland && !emergency){// changed == 0 to < 0.1d

                /*if(falling){
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                            "[Sugarcane Helper] : " + EnumChatFormatting.DARK_GREEN + "New layer detected"));
                    mode = 1- mode;

                }
                else*/ if((mc.thePlayer.posZ != initialZ && mc.thePlayer.posX != initialX)) {
                    ExecuteRunnable(changeMotion);
                }
            }


            // Processes //
            if (process1 && !process3 && !process4) {
                if (shdBePressingKey) {

                    KeyBinding.setKeyBindState(keybindAttack, true);
                    KeyBinding.setKeyBindState(keybindS, false);
                    error = false;
                    KeyBinding.setKeyBindState(keybindD, true);
                    KeyBinding.setKeyBindState(keybindA, false);
                    KeyBinding.setKeyBindState(keybindW, false);
                    if(Config.Direction == DirectionEnum.DtoS) {
                        if (!setspawned) {
                            mc.thePlayer.sendChatMessage("/setspawn");
                            setspawned = true;
                        }
                    } else setspawned = false;
                }

            } else if (process2 && !process3 && !process4) {
                if (shdBePressingKey) {

                    KeyBinding.setKeyBindState(keybindAttack, true);
                    KeyBinding.setKeyBindState(keybindS, false);
                    KeyBinding.setKeyBindState(keybindA, true);
                    KeyBinding.setKeyBindState(keybindD, false);
                    KeyBinding.setKeyBindState(keybindW, false);
                    if(Config.Direction == DirectionEnum.AtoS) {
                        if (!setspawned) {
                            mc.thePlayer.sendChatMessage("/setspawn");
                            setspawned = true;
                        }
                    } else setspawned = false;

                }

            }
            if(process3 && !process4){
                if (shdBePressingKey) {

                    KeyBinding.setKeyBindState(keybindAttack, true);
                    KeyBinding.setKeyBindState(keybindA, false);
                    KeyBinding.setKeyBindState(keybindD, false);
                    KeyBinding.setKeyBindState(keybindW, false);
                    KeyBinding.setKeyBindState(keybindS, true);
                    if(Config.Direction == DirectionEnum.StoD || Config.Direction == DirectionEnum.StoA) {
                        if (!setspawned) {
                            mc.thePlayer.sendChatMessage("/setspawn");
                            setspawned = true;
                        }
                    } else setspawned = false;
                }

            } else if(process4){

                if(shdBePressingKey) {
                    KeyBinding.setKeyBindState(keybindA, false);
                    KeyBinding.setKeyBindState(keybindD, false);
                    KeyBinding.setKeyBindState(keybindW, false);
                    KeyBinding.setKeyBindState(keybindW, true);
                    KeyBinding.setKeyBindState(keybindS, false);

                }
            }


        } else{
            locked = false;
        }



    }


    //multi-threads

    Runnable checkChange = new Runnable() {
        @Override
        public void run() {

            if(!notInIsland && !emergency && enabled) {
                deltaX = Math.abs(mc.thePlayer.posX - beforeX);
                deltaZ = Math.abs(mc.thePlayer.posZ - beforeZ);

                beforeX = mc.thePlayer.posX;
                beforeZ = mc.thePlayer.posZ;

                ScheduleRunnable(checkChange, 3, TimeUnit.SECONDS);

            }

        }
    };

    Runnable changeMotion = new Runnable() {
        @Override
        public void run() {
            if(!notInIsland && !emergency) {
                initialX = mc.thePlayer.posX;
                initialZ = mc.thePlayer.posZ;

                if(Config.Direction == DirectionEnum.DtoS || Config.Direction == DirectionEnum.StoD)
                process1 = !process1;
                else
                process2 = !process2;

                process3 = !process3;

                set = false;
            }
        }
    };

    Runnable stopAntistuck = new Runnable() {
        @Override
        public void run() {
            deltaX = 10000;
            deltaZ = 10000;
            process4 = false;
            setAntiStuck = false;
        }
    };

    Runnable LeaveSBIsand = new Runnable() {
        @Override
        public void run() {
            mc.thePlayer.sendChatMessage("/l");
            ScheduleRunnable(Rejoin, 8, TimeUnit.SECONDS);
        }
    };

    Runnable Rejoin = new Runnable() {
        @Override
        public void run() {
            mc.thePlayer.sendChatMessage("/play sb");
            ScheduleRunnable(WarpHome, 8, TimeUnit.SECONDS);
        }
    };

    Runnable WarpHome = new Runnable() {
        @Override
        public void run() {
            mc.thePlayer.sendChatMessage("/warp home");
            ScheduleRunnable(afterRejoin1, 8, TimeUnit.SECONDS);
        }
    };



    Runnable afterRejoin1 = new Runnable() {
        @Override
        public void run() {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            if(!error) {
                ScheduleRunnable(afterRejoin2, 2500, TimeUnit.MILLISECONDS);
            } else {
                ScheduleRunnable(WarpHome, 5, TimeUnit.SECONDS);
                error = false;
            }

        }
    };
    Runnable afterRejoin2 = new Runnable() {
        @Override
        public void run() {

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);

            initialize();

            mc.inGameHasFocus = true;
            mouseHelper.grabMouseCursor();
            mc.displayGuiScreen((GuiScreen)null);
            Field f = null;
            f = FieldUtils.getDeclaredField(mc.getClass(), "leftClickCounter",true);
            try {
                f.set(mc, 10000);
            }catch (Exception e){
                e.printStackTrace();
            }

            ScheduleRunnable(checkChange, 3, TimeUnit.SECONDS);
        }
    };
    Runnable checkPriceChange = new Runnable() {
        @Override
        public void run() {

            if(!(prevMoney == -999) && (totalMoney - prevMoney >= 0)) {
                moneyper10sec = totalMoney - prevMoney;
            }

            prevMoney = totalMoney;

            ScheduleRunnable(checkPriceChange, 10, TimeUnit.SECONDS);
        }
    };

    @SubscribeEvent
    public void OnKeyPress(InputEvent.KeyInputEvent event){


        if (customKeyBinds[0].isPressed()) {
            mc.displayGuiScreen(new GUI());
        }
        if (customKeyBinds[1].isPressed()) {
            if (!enabled)
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                        "[Sugarcane Helper] : " + EnumChatFormatting.DARK_GREEN + "Starting script"));

            toggle();
        }



    }

    Runnable EMERGENCY = new Runnable() {
        @Override
        public void run() {

            KeyBinding.setKeyBindState(keybindAttack, false);
            KeyBinding.setKeyBindState(keybindA, false);
            KeyBinding.setKeyBindState(keybindW, false);
            KeyBinding.setKeyBindState(keybindD, false);
            KeyBinding.setKeyBindState(keybindS, false);

            // mc.thePlayer.addChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(SHUTDOWN, 4123, TimeUnit.MILLISECONDS);


        }
    };

    Runnable SHUTDOWN = new Runnable() {
        @Override
        public void run() {
            mc.shutdown();
        }
    };

    void toggle(){

        mc.thePlayer.closeScreen();
        if(enabled){
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                    "[Sugarcane Helper] : " + EnumChatFormatting.DARK_GREEN + "Stopped script"));
            stop();
        } else {
            playerYaw = Math.round((Utils.get360RotationYaw() + 45)/ 90) * 90 - 45;
        }
        enabled = !enabled;
    }
    void stop(){
        net.minecraft.client.settings.KeyBinding.setKeyBindState(keybindA, false);
        net.minecraft.client.settings.KeyBinding.setKeyBindState(keybindW, false);
        net.minecraft.client.settings.KeyBinding.setKeyBindState(keybindD, false);
        net.minecraft.client.settings.KeyBinding.setKeyBindState(keybindS, false);
        net.minecraft.client.settings.KeyBinding.setKeyBindState(keybindAttack, false);
    }
    void activateFailsafe(){
        shdBePressingKey = false;
        notInIsland = true;
        KeyBinding.setKeyBindState(keybindAttack, false);
        process1 = false;
        process2 = false;
        process3 = false;
        process4 = false;

    }
    Block playerBlock(){
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
    }

    void ScheduleRunnable(Runnable r, int delay, TimeUnit tu){
        ScheduledExecutorService eTemp = Executors.newScheduledThreadPool(1);
        eTemp.schedule(r, delay, tu);
        eTemp.shutdown();
    }
    void ExecuteRunnable(Runnable r){
        ScheduledExecutorService eTemp = Executors.newScheduledThreadPool(1);
        eTemp.execute(r);
        eTemp.shutdown();
    }
    void initialize(){
        deltaX = 10000;
        deltaZ = 10000;

        process1 = false;
        process2 = false;
        process3 = false;
        switch (Config.Direction){
            case AtoS:
                process2 = true;
                break;
            case StoA:
                process3 = true;
                break;
            case StoD:
                process3 = true;
                break;
            case DtoS:
                process1 = true;
                break;
        }
        process4 = false;
        setspawned = false;

        shdBePressingKey = true;
        notInIsland = false;
        beforeX = mc.thePlayer.posX;
        beforeZ = mc.thePlayer.posZ;
        initialX = mc.thePlayer.posX;
        initialZ = mc.thePlayer.posZ;
        set = false;
    }



}
