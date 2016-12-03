package com.github.unchama.buildassist;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryListener implements Listener {
	HashMap<UUID,PlayerData> playermap = BuildAssist.playermap;

	/*
	//プレイヤーが4次元ポケットを閉じた時に実行
	@EventHandler
	public void onPlayerPortalCloseEvent(InventoryCloseEvent event){
		HumanEntity he = event.getPlayer();
		Inventory inventory = event.getInventory();

		//インベントリを開けたのがプレイヤーではない時終了
		if(!he.getType().equals(EntityType.PLAYER)){
			return;
		}
		//インベントリサイズが２７でない時終了
		if(inventory.getSize() != 27){
			return;
		}
		if(inventory.getTitle().equals(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "4次元ポケット")){
			Player player = (Player)he;
			PlayerInventory pinventory = player.getInventory();
			ItemStack itemstack = pinventory.getItemInMainHand();
			if(itemstack.getType().equals(Material.ENDER_PORTAL_FRAME)){
				//閉まる音を再生
				player.playSound(player.getLocation(), Sound.BLOCK_ENDERCHEST_CLOSE, 1, (float) 0.1);
			}
		}
	}
	*/

	@EventHandler
	public void onPlayerClickActiveSkillSellectEvent(InventoryClickEvent event){
		//外枠のクリック処理なら終了
		if(event.getClickedInventory() == null){
			return;
		}

		ItemStack itemstackcurrent = event.getCurrentItem();
		InventoryView view = event.getView();
		HumanEntity he = view.getPlayer();
		//インベントリを開けたのがプレイヤーではない時終了
		if(!he.getType().equals(EntityType.PLAYER)){
			return;
		}

		Inventory topinventory = view.getTopInventory();
		//インベントリが存在しない時終了
		if(topinventory == null){
			return;
		}
		//インベントリサイズが36でない時終了
		if(topinventory.getSize() != 36){
			return;
		}
		Player player = (Player)he;
		UUID uuid = player.getUniqueId();
		PlayerData playerdata = playermap.get(uuid);

		//インベントリ名が以下の時処理
		if(topinventory.getTitle().equals(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "木の棒メニューB")){
			event.setCancelled(true);

			//プレイヤーインベントリのクリックの場合終了
			if(event.getClickedInventory().getType().equals(InventoryType.PLAYER)){
				return;
			}

			//経験値変更用のクラスを設定
			ExperienceManager expman = new ExperienceManager(player);

			/*
			 * クリックしたボタンに応じた各処理内容の記述ここから
			 */


			if(itemstackcurrent.getType().equals(Material.FEATHER)){
				if(itemstackcurrent.getAmount() == 1){
					//fly 1分予約追加
					player.closeInventory();
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					player.chat("/fly 1");
				}else if(itemstackcurrent.getAmount() == 5){
					//fly 5分予約追加
					player.closeInventory();
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					player.chat("/fly 5");
				}

			} else if (itemstackcurrent.getType().equals(Material.ELYTRA)){
				//fly ENDLESSモード
				player.closeInventory();
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
				player.chat("/fly endless");

			} else if (itemstackcurrent.getType().equals(Material.CHAINMAIL_BOOTS)){
				//fly OFF
				player.closeInventory();
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
				player.chat("/fly finish");

			} else if (itemstackcurrent.getType().equals(Material.STONE)){
				//範囲設置スキル ON/OFF
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
				if(playerdata.ZoneSetSkillFlag == false){
					playerdata.ZoneSetSkillFlag = true ;
					player.sendMessage(ChatColor.RED + "範囲設置スキルON" ) ;
					player.openInventory(MenuInventoryData.getMenuData(player));
				}else if (playerdata.ZoneSetSkillFlag == true ){
					playerdata.ZoneSetSkillFlag = false ;
					player.sendMessage(ChatColor.RED + "範囲設置スキルOFF" ) ;
					player.openInventory(MenuInventoryData.getMenuData(player));
				}
			} else if (itemstackcurrent.getType().equals(Material.SKULL_ITEM)){
				//ホームメニューへ帰還
				player.playSound(player.getLocation(), Sound.BLOCK_FENCE_GATE_OPEN, 1, (float) 0.1);
				player.openInventory(MenuInventoryData.getSetBlockSkillData(player));
				
			} else if (itemstackcurrent.getType().equals(Material.COMPASS)){
				//サブホームに移動
				if(playerdata.sub_home != null){
					World world = Bukkit.getWorld(playerdata.sub_home.getWorld().getName());
					if(world != null){
						player.teleport(playerdata.sub_home);
						player.sendMessage("サブホームポイントにワープしました");
					}else{
						player.sendMessage("サブホームポイントが設定されてません");
					}
				}else{
					player.sendMessage("サブホームポイントが設定されてません");
				}
			} else if (itemstackcurrent.getType().equals(Material.BED)){
				//ホームをセット
				playerdata.sub_home = player.getLocation();
				player.sendMessage("現在位置をサブホームポイントに設定しました");

			}
			
			
		}
		//インベントリ名が以下の時処理
		if(topinventory.getTitle().equals(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "「範囲設置スキル」設定画面")){
			event.setCancelled(true);

			//プレイヤーインベントリのクリックの場合終了
			if(event.getClickedInventory().getType().equals(InventoryType.PLAYER)){
				return;
			}
			/*
			 * クリックしたボタンに応じた各処理内容の記述ここから
			 */

			if(itemstackcurrent.getType().equals(Material.BARRIER)){
				//ホームメニューへ帰還
				player.playSound(player.getLocation(), Sound.BLOCK_FENCE_GATE_OPEN, 1, (float) 0.1);
				player.openInventory(MenuInventoryData.getMenuData(player));

			}else if(itemstackcurrent.getType().equals(Material.SKULL_ITEM)){
				if(itemstackcurrent.getAmount() == 11){
					//範囲MAX
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					playerdata.AREAint = 5;
					player.sendMessage(ChatColor.RED + "現在の範囲設定は"+(playerdata.AREAint *2 +1)+"×"+ (playerdata.AREAint *2 +1)+"です");
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));

				}else if(itemstackcurrent.getAmount() == 7){
					//範囲++
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					if(playerdata.AREAint == 5){
						player.sendMessage(ChatColor.RED + "[範囲スキル設定]これ以上範囲を広くできません！" ) ;
					}else {
						playerdata.AREAint ++ ;
					}
					player.sendMessage(ChatColor.RED + "現在の範囲設定は"+(playerdata.AREAint *2 +1)+"×"+ (playerdata.AREAint *2 +1)+"です");
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));

				}else if(itemstackcurrent.getAmount() == 5){
					//範囲初期化
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					playerdata.AREAint = 2;
					player.sendMessage(ChatColor.RED + "現在の範囲設定は"+(playerdata.AREAint *2 +1)+"×"+ (playerdata.AREAint *2 +1)+"です");
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));

				}else if(itemstackcurrent.getAmount() == 3){
					//範囲--
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					if(playerdata.AREAint == 1){
						player.sendMessage(ChatColor.RED + "[範囲スキル設定]これ以上範囲を狭くできません！" ) ;
					}else {
						playerdata.AREAint -- ;
					}
					player.sendMessage(ChatColor.RED + "現在の範囲設定は"+(playerdata.AREAint *2 +1)+"×"+ (playerdata.AREAint *2 +1)+"です");
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));

				}else if(itemstackcurrent.getAmount() == 1){
					//範囲MIN
					player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
					playerdata.AREAint = 1;
					player.sendMessage(ChatColor.RED + "現在の範囲設定は"+(playerdata.AREAint *2 +1)+"×"+ (playerdata.AREAint *2 +1)+"です");
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));
				}
			} else if (itemstackcurrent.getType().equals(Material.STONE)){
				//範囲設置スキル ON/OFF
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
				if(playerdata.ZoneSetSkillFlag == false){
					playerdata.ZoneSetSkillFlag = true ;
					player.sendMessage(ChatColor.RED + "範囲設置スキルON" ) ;
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));
				}else if (playerdata.ZoneSetSkillFlag == true ){
					playerdata.ZoneSetSkillFlag = false ;
					player.sendMessage(ChatColor.RED + "範囲設置スキルOFF" ) ;
					player.openInventory(MenuInventoryData.getSetBlockSkillData(player));
				}

			}

		}

	}


}