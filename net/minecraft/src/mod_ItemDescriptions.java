package net.minecraft.src;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import au.com.bytecode.opencsv.CSVReader;

import net.minecraft.client.Minecraft;

public class mod_ItemDescriptions extends BaseMod {
	
	public static List itemDescriptions2;
	private static final File descriptionDir = new File(Minecraft.getMinecraftDir(), "/config/itemdescriptions/");
	private static final File vanillaCsv = new File(Minecraft.getMinecraftDir(), "/config/itemdescriptions/vanilla.csv");
	
	
	public mod_ItemDescriptions() {
		try{
			CSVReader reader = new CSVReader(new FileReader(vanillaCsv));
			itemDescriptions2 = reader.readAll();
			ModLoader.getLogger().fine("Vanilla.csv descriptions loaded");
			
			File file[] = descriptionDir.listFiles();
			for(int i = 0; i < file.length; i++)
			{
				File file1 = file[i];
				
				if(!file1.getName().equalsIgnoreCase("vanilla.csv") && file1.getName().endsWith(".csv"))
				{
					System.out.println(file1.getName());
					reader = new CSVReader(new FileReader(file1));
					itemDescriptions2.addAll(reader.readAll());
					ModLoader.getLogger().fine(file1.getName()+" descriptions loaded");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String test = getItemDescription("item..structure.box_controller.0", 0);
		System.out.println(test);
	}
	
	private static Pattern descriptionPattern = Pattern.compile("(.*\\..*)\\.(.*)\\.(.*)");
	private static Pattern itemnamePattern = Pattern.compile("(.*?\\..*)\\.(.*)");
	private static Pattern rangePattern = Pattern.compile("([0-9]*)-([0-9]*)");
	private static Matcher matcher;
	private static Matcher matcher1;
	private static Matcher matcher2;
	private static String lastItemName;
	private static String lastItemDescription;
	private static int lastDataVal;
	
	public static String getItemDescription(String itemName, int dataval)
	{
		if(itemDescriptions2 == null || itemDescriptions2.size() == 0)
			return "";
		
		if(lastItemName != null && itemName == lastItemName && dataval == lastDataVal)
			return lastItemDescription;
		
		String[] splitItemname = new String[2];
		matcher2 = itemnamePattern.matcher(itemName);
		while(matcher2.find())
		{
			splitItemname[0] = matcher2.group(1);
			splitItemname[1] = matcher2.group(2);
		}
		
		for(int i = 0; i < itemDescriptions2.size(); i++)
		{
			String entry[] = (String[])itemDescriptions2.get(i);
			if(entry.length >= 3)
			{
				matcher = descriptionPattern.matcher(entry[0]);
				while (matcher.find()) {
					if(matcher.group(1).equalsIgnoreCase(splitItemname[0]) && matcher.group(3).equalsIgnoreCase("1"))
					{
						matcher1 = rangePattern.matcher(matcher.group(2));
						while(matcher1.find()) {
							int low = Integer.parseInt(matcher1.group(1));
							int high = Integer.parseInt(matcher1.group(2));
							if(dataval >= low && dataval <= high)
							{
								lastItemName = itemName;
								lastDataVal = dataval;
								lastItemDescription = entry[2];
								return entry[2];
							}
						}
						
						if(matcher.group(2).equalsIgnoreCase(splitItemname[1]) || matcher.group(2).equalsIgnoreCase("*"))
						{
							lastItemName = itemName;
							lastDataVal = dataval;
							lastItemDescription = entry[2];
							return entry[2];
						} else {
							break;
						}
					}
				}
			}
		}
		
		return "";
	}
	
	
	private static String lastItemName1;
	private static Map lastCollatedRecipe;
	
	public static Map getCollatedRecipe(ItemStack itemstack)
	{
		String itemName = itemstack.getItem().getItemNameIS(itemstack);
		if(lastItemName1 != null && lastItemName1.equalsIgnoreCase(itemName))
		{
			return lastCollatedRecipe;
		}
		
		List recipes = CraftingManager.getInstance().getRecipeList();
		Map<Integer, Integer[]> collatedRecipe = new HashMap<Integer, Integer[]>();
		for(int i = 0; i < recipes.size(); i++)
		{
			IRecipe irecipe = (IRecipe)recipes.get(i);
			ItemStack output = irecipe.getRecipeOutput();
			if(output.itemID == itemstack.itemID && output.getItemDamage() == itemstack.getItemDamage())
			{
				ItemStack[] recipeItems = getRecipeItemStackArray(irecipe);
				// Collate each item together to get total stack size per item.
		    	for(int i1 = 0; i1 < recipeItems.length; i1++)
		    	{
		    		if(recipeItems[i1] != null)
		    		{
		        		if(collatedRecipe.containsKey(recipeItems[i1].itemID))
		        		{
		        			Integer vals[] = (Integer[])collatedRecipe.get(recipeItems[i1].itemID);
		        			vals[0] += 1; 
		        			collatedRecipe.put(recipeItems[i1].itemID, vals);
		        		} else {
		        			Integer vals[] = {1, recipeItems[i1].getItemDamage()};
		        			collatedRecipe.put((Integer)recipeItems[i1].itemID, vals);
		        		}
		    		}
		    	}
		    	break;
			}
		}
		
		lastItemName1 = itemName;
		lastCollatedRecipe = collatedRecipe;
		return collatedRecipe;
	}
	
	public static ItemStack[] getRecipeItemStackArray(IRecipe irecipe)
	{
		ItemStack itemstacks[];
		try
		{
			if(irecipe instanceof ShapedRecipes)
	        {
				itemstacks = (ItemStack[])ModLoader.getPrivateValue(ShapedRecipes.class, (ShapedRecipes)irecipe, 2);
	        }else
	    	{
	        	ArrayList recipeItems = new ArrayList((List)ModLoader.getPrivateValue(ShapelessRecipes.class, (ShapelessRecipes)irecipe, 1));
	        	itemstacks = (ItemStack[])recipeItems.toArray(new ItemStack[recipeItems.size()]);
	    	}
			return itemstacks;
		} catch(NoSuchFieldException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1.1";
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

}
