package io.github.vexo.features.chat


import io.github.vexo.config.BooleanSetting
import io.github.vexo.config.DropdownSetting
import io.github.vexo.config.KeybindSetting
import io.github.vexo.config.Module
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.events.PacketEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object ChatCleaner : Module(
    name = "Chat Cleaner",
    description = "Hides Useless Messages",
    category = "Chat"
){
    private val RandomSpam = registerSetting(BooleanSetting("Random Spam", true, "Schaltet das Feature ein/aus"))
    private val RandomDungeonSpawn = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))
    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {

        if (RandomSpam.value && RandomSpamRegex.any { it.containsMatchIn(event.message) }) {
            event.setCanceled(true)
        }
        if (RandomDungeonSpawn.value && RandomDungeonSpawnRegex.any { it.containsMatchIn(event.message) }) {
            event.setCanceled(true)
        }
    }

    private val RandomSpamRegex = listOf(
        Regex("Your pickaxe ability is on cooldown for .+s."),
        Regex("AUTO-PICKUP! Drop sent to your inventory! \\[I GET IT\\]"),
        Regex("Warping you to your SkyBlock island..."),
        Regex("You earned .+ Event EXP from playing SkyBlock!"),
        Regex("Warping..."),
        Regex("Watchdog has banned .+ players in the last 7 days."),
        Regex("RARE REWARD!.+"),
        Regex("You are playing on profile: .+"),
        Regex("Profile ID:.+"),
        Regex("Whow! Slow down there!"),
        Regex("Woah slow down, you're doing that too fast!"),
        Regex("Command Failed: This command is on cooldown! Try again in about a second!"),
        Regex("Autopet equipped your.+"),
        Regex("Your Auto Recombobulator recombobulated"),
        Regex("Blacklisted modifications are a bannable offense!"),
        Regex("\\[WATCHDOG ANNOUNCEMENT\\]"),
        Regex("Staff have banned an additional .+"),
        Regex("You sold .+ x.* for .+"),
        Regex("You don't have enough space in your inventory to pick up this item!.+"),
        Regex("Inventory full\\? Don't forget to check out your Storage inside the SkyBlock Menu!"),
        Regex("You are not allowed to use Potion Effects.+"),
        Regex("You summoned your.+"),
        Regex("Moved .+ Ender Pearl from your Sacks to your inventory."),
        Regex("There are blocks in the way!"),
        Regex("Click here to view them!"),
        Regex(".+ joined the lobby! .*"),
        Regex("Welcome to Hypixel SkyBlock!"),
        Regex("Latest update: SkyBlock .+"),
        Regex("BONUS! Temporarily earn 5% more skill experience!"),
        Regex("Sending to server .+"),
        Regex("Queuing... .+"),
        Regex("Your .+ hit .+ for [\\d,.]+ damage."),
        Regex("You do not have enough mana to do this!"),
        Regex("\\w+ Kill Combo+"),
        Regex("You earned .+ GEXP .*"),
        Regex("This menu is disabled here!"),
        Regex("This item is on cooldown.+"),
        Regex("This ability is on cooldown.+"),
        Regex("Please wait a few seconds between refreshing!"),
        Regex("You cannot hit the silverfish while it's moving!"),
        Regex("Your Kill Combo has expired! You reached a .+ Kill Combo!"),
        Regex("Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! You are not allowed to use existing Potion Effects while in Dungeons."),
        Regex("FISHING FESTIVAL The festival is now underway! Break out your fishing rods and watch out for sharks!"),
        Regex("Attempting to add you to the party..."),
        Regex("Mythological Rituals! A mythological creature spawned!")

    )
    private val RandomDungeonSpawnRegex = listOf(
        Regex("Creeper Veil Activated!"),
        Regex("Creeper Veil De-activated!"),
        Regex("\\w+ has obtained Revive Stone!"),
        Regex("\\[NPC\\] Mort: .+"),
        Regex("The Crusher hit you for .+ damage!"),
        Regex("Your Spirit Pet healed .+ for .+ health!"),
        Regex(".+ Granted you .+"),
        Regex("Goldor's TNT Trap hit you for 1,788.9 true damage."),
        Regex("A Blood Key was picked up"),
        Regex("This Terminal doesn't seem to be responsive at the moment."),
        Regex("⚠ Maxor is enraged! ⚠"),
        Regex("⚠ Storm is enraged! ⚠"),
        Regex("Giga Lightning.+"),
        Regex("Necron's Nuclear Frenzy hit you for .+ damage."),
        Regex("Someone has already activated this lever!"),
        Regex("Goldor's Greatsword hit you for .+ damage."),
        Regex("A mystical force in this room prevents you from using that ability!"),
        Regex("The Frozen Adventurer used Ice Spray on you!"),
        Regex("It isn't your turn!"),
        Regex("That chest is locked!"),
        Regex("Don't move diagonally! Bad!"),
        Regex("Oops! You stepped on the wrong block!"),
        Regex("A shiver runs down your spine..."),
        Regex("The BLOOD DOOR has been opened!"),
        Regex("Your Ultimate is currently on cooldown for .+ more seconds."),
        Regex("ESSENCE! .+ found .+ Essence!"),
        Regex("This lever has already been used."),
        Regex("You hear the sound of something opening..."),
        Regex("This chest has already been searched!"),
        Regex(".+ has obtained .+!"),
        Regex(".*Also granted you.+"),
        Regex("The Lost Adventurer used Dragon's Breath on you!"),
        Regex("Throwing Axe is now available!"),
        Regex("Used Throwing Axe!"),
        Regex("\\[STATUE\\] Oruo the Omniscient: [^I am Oruo the Omniscient.].+"),
        Regex("\\[NPC\\] (Hugo)"),
        Regex("PUZZLE SOLVED!.+"),
        Regex("DUNGEON BUFF! .+"),
        Regex("A Crypt Wither Skull exploded, hitting you for .+ damage."),
        Regex("\\w+ opened a WITHER door!"),
        Regex("\\[(Tank|Healer|Mage|Archer|Berserk)+\\] .+"),
        Regex("\\[SKULL\\] .+"),
        Regex("\\[BOMB\\] Creeper:.+"),
        Regex("\\[Boss\\].+"),
        Regex("\\[Sacks\\] .+ item.+"),
        Regex("The .+ Trap hit you for .+ damage!"),
        Regex("Healer Milestone.+"),
        Regex("Archer Milestone.+"),
        Regex("Mage Milestone.+"),
        Regex("Tank Milestone.+"),
        Regex("Berserk Milestone.+"),
        Regex("RARE DROP!.+"),
        Regex("Your .+ stats are doubled because you are the only player using this class!"),
        Regex(".+ is now available!"),
        Regex(".+ is now ready!"),
        Regex("\\w+ Milestone .+:.+ "),
        Regex("Your CLASS stats are doubled because you are the only player using this class!"),
        Regex("RIGHT CLICK on .+ to open it. .+"),
        Regex("Thunderstorm is ready to use! Press DROP to activate it!"),
        Regex(".+ unlocked .+ Essence!"),
        Regex(".+ unlocked .+ Essence x\\d+!"),
        Regex("You do not have the key for this door!"),
        Regex("The Stormy .+ struck you for .+ damage!"),
        Regex("You cannot move the silverfish in that direction!"),
        Regex("You cannot hit the silverfish while it's moving!"),
        Regex(".+ has obtained Blood Key!"),
        Regex("The Flamethrower hit you for .+ damage!"),
        Regex(".+ found a Wither Essence! Everyone gains an extra essence!"),
        Regex(".+ is ready to use! Press DROP to activate it!"),
        Regex("This creature is immune to this kind of magic!"),
        Regex("Moved .+ from your Sacks to your inventory."),
        Regex("◕ \\w+ picked up your .+ Orb!"),
        Regex("Your tether with .+ healed you for .+ health.")

    )

}