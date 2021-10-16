/*
 * Copyright (c) 2018, SomeoneWithAnInternetConnection
 * Copyright (c) 2018, oplosthee <https://github.com/oplosthee>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.onetickflick;

import com.google.inject.Provides;
import java.util.Timer;
import java.util.TimerTask;
import net.runelite.client.callback.ClientThread;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "One Tick Flick Metronome",
	description = "A metronome timed specifically to aid with one tick prayer flicking.",
	tags = {"tick", "timers"},
	enabledByDefault = false
)
public class OneTickFlickPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OneTickFlickPluginConfiguration config;

	@Inject
	private ClientThread clientThread;

	private long tickDelayMS=85;
	private long tockDelayMS=515;

	private Timer tickTimer = new Timer("Tick Timer");
	private Timer tockTimer = new Timer("Tock Timer");

	@Provides
	OneTickFlickPluginConfiguration provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OneTickFlickPluginConfiguration.class);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		Preferences preferences = client.getPreferences();
		TimerTask delayedTick = new TimerTask() {
			public void run() {
				int previousVolume = preferences.getSoundEffectVolume();
				preferences.setSoundEffectVolume(config.tickVolume());
				clientThread.invoke(() -> {
					client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.tickVolume());
				});
				preferences.setSoundEffectVolume(previousVolume);
			}
		};

		TimerTask delayedTock = new TimerTask() {
			public void run() {
				int previousVolume = preferences.getSoundEffectVolume();
				preferences.setSoundEffectVolume(config.tockVolume());
				clientThread.invoke(() -> {
					client.playSoundEffect(SoundEffectID.GE_DECREMENT_PLOP, config.tockVolume());
				});
				preferences.setSoundEffectVolume(previousVolume);
			}
		};

		tickTimer.schedule(delayedTick,tickDelayMS);

		tockTimer.schedule(delayedTock,tockDelayMS);


	}
}
