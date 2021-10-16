package com.onetickflick;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OneTickFlickPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OneTickFlickPlugin.class);
		RuneLite.main(args);
	}
}