package net.minecraft.src;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class GuiItemDescriptions extends GuiScreen
{
	
	protected static RenderItem itemRenderer = new RenderItem();
    protected static boolean mouseOverItem;
    public static boolean itemDesciptionsEnabled = true;
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;
    protected int xSize1;
    protected int ySize1;
    
	public GuiItemDescriptions() {
		xSize1 = 121;
        ySize1 = 162;
        Minecraft minecraft = ModLoader.getMinecraftInstance();
        minecraft.displayGuiScreen(this);
	}
	
	public void initGui()
    {
        super.initGui();
    }
	
	public void setSize(int x, int y)
	{
		xSize = x;
        ySize = y;
        guiLeft = -xSize1 - 5;
        guiTop = (y/2) - (ySize1/2);
	}
	
	public void drawDescriptionBackground(int i, int j)
    {
		int x = guiLeft;
		int y = guiTop;
        int k = mc.renderEngine.getTexture("/gui/itemdescriptionbackground.png");
        mc.renderEngine.bindTexture(k);
        if(mouseOverItem)
        {
        	drawTexturedModalRect(x, y, 0, 0, xSize1, ySize1);
        }
    }
	
	public void drawDescriptions(int i, int j, ItemStack descItem)
	{
		mouseOverItem = false;
        RenderHelper.func_41089_c();
        GL11.glPushMatrix();
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapEnabled, (float)240 / 1.0F, (float)240 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if(descItem != null && descItem.getItem() != null)
    	{
        	mouseOverItem = true;
        	Item item = descItem.getItem();
    		String itemname = item.getItemDisplayName(descItem);
    		String itemcodename = item.getItemNameIS(descItem);
    		String description = "";
    		
    		if(itemcodename == null)
    			itemcodename = "item."+itemname;
    		
    		if(!itemcodename.matches("(.*?\\..*)\\.(.*)"))
    			itemcodename += "." + descItem.getItemDamage();
    		
    		if(item != null && item instanceof ItemBlock)
    		{
    			Block block = Block.blocksList[((ItemBlock) item).getBlockID()];
    			if(block != null && block instanceof ICraftingDescription)
    			{
    				description = ((ICraftingDescription)block).getDescription( descItem.getItemDamage() );
    			}
    		}
    		
    		if(description == "")
    		{
        		if(item != null && item instanceof ICraftingDescription)
        		{
        			description = ((ICraftingDescription)item).getDescription( descItem.getItemDamage() );
        		} else {
        			description = mod_ItemDescriptions.getItemDescription(itemcodename, descItem.getItemDamage());
        		}
    		}
    		
    		if(itemname.length() > 28)
    			itemname = itemname.substring(0, 27) + "...";
    		
    		float scalef = 0.5F;
    		int titleLeft = guiLeft + 5;
    		int titleTop  = guiTop + 5;
    		int descLeft = MathHelper.floor_float(titleLeft/scalef);
    		int descTop = MathHelper.floor_float(titleTop/scalef);
    		fontRenderer.drawSplitString(itemname, titleLeft, titleTop, 90, -1);
    		
    		GL11.glPushMatrix();
    		GL11.glScalef(scalef, scalef, 1F);
    		GL11.glTranslatef(descLeft, descTop, 0);
        	fontRenderer.drawSplitString(description, 0, 40, 180, -1);
        	fontRenderer.drawSplitString("Code Name:", 0, 285, 180, -1);
        	fontRenderer.drawSplitString(itemcodename+".1", 0, 295, 180, -1);
        	GL11.glPopMatrix();
        	
        	displayRecipe(i, j, descItem);
    	}
        GL11.glPopMatrix();
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
	}
	
	public void displayRecipe(int i, int j, ItemStack itemstack)
	{
		if(itemstack != null)
        {
			Map<Integer, Integer[]> collatedRecipe = mod_ItemDescriptions.getCollatedRecipe(itemstack);
        	if(collatedRecipe != null)
        	{
        		int y = 0;
        		mouseOverItem = true;
        		for (Map.Entry<Integer, Integer[]> entry : collatedRecipe.entrySet())
        		{
        			Integer vals[] = entry.getValue();
        			itemstack = new ItemStack(entry.getKey(), vals[0], vals[1]);
        			GL11.glTranslatef(0.0F, 0.0F, 32F);
        			int x1 = guiLeft + 99;
        			int y1 = guiTop + 5;
        			zLevel = 200F;
                    itemRenderer.zLevel = 200F;
        			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, itemstack, x1, y1+y);
            		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, x1, y1+y);
            		zLevel = 0F;
                    itemRenderer.zLevel = 0F;
            		y += 18;
        		}
        	}
        }
	}

}
