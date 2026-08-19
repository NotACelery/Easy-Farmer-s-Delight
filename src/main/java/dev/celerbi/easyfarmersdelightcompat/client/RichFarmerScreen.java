package dev.celerbi.easyfarmersdelightcompat.client;

import dev.celerbi.easyfarmersdelightcompat.menu.RichFarmerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class RichFarmerScreen extends AbstractContainerScreen<RichFarmerMenu> {
    private static final ResourceLocation BACKGROUND=ResourceLocation.fromNamespaceAndPath("easy_villagers","textures/gui/container/output.png");
    public RichFarmerScreen(RichFarmerMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=133;inventoryLabelY=40;titleLabelY=9;}
    @Override public void render(GuiGraphics g,int mx,int my,float pt){super.render(g,mx,my,pt);renderTooltip(g,mx,my);}
    @Override protected void renderBg(GuiGraphics g,float pt,int mx,int my){int x=(width-imageWidth)/2,y=(height-imageHeight)/2;g.blit(BACKGROUND,x,y,0,0,imageWidth,imageHeight);g.blit(BACKGROUND,x+141,y+19,51,19,18,18);}
}
