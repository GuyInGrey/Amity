package com.GuyInGrey.Amity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Buttons implements Listener
{
  @EventHandler
  public void onInteract(PlayerInteractEntityEvent e)
  {
    if (e.getPlayer().isSneaking())
    {
      return;
    }
    if (e.getRightClicked() == null)
    {
      return;
    }
    if (e.getRightClicked().getType() != EntityType.ITEM_FRAME)
    {
      return;
    }

    ItemFrame item = (ItemFrame) e.getRightClicked();
    if (item.getItem().getType() == Material.AIR)
    {
      return;
    }
    String name = item.getItem().getItemMeta().getDisplayName().trim().toLowerCase();
    e.setCancelled(true);

    HashMap<String, Consumer<Player>> actions = new HashMap<String, Consumer<Player>>();

    actions.put("lobby", (p) -> Amity.instance.BtnLobby(p));
    actions.put("tag", (p) -> Tag.instance.TagLobby(p));
    actions.put("[tag] startgame", (p) -> Tag.instance.StartGamePressed());
    actions.put("[tag] explore", (p) -> Tag.instance.Explore(p));
    actions.put("[tag] howtoplay", (p) -> Tag.instance.HowToPlay(p));
    actions.put("[tag] spectate", (p) -> Tag.instance.Spectate(p));

    for (Entry<String, Consumer<Player>> ent : actions.entrySet())
    {
      if (name.contentEquals(ent.getKey()))
      {
        ent.getValue().accept(e.getPlayer());
        return;
      }
    }
  }
}