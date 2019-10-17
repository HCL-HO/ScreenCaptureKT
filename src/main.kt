import ConfigLoaderKT.ConfigLoader
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

const val PATH = "C:\\Users\\eric_cl_ho\\Desktop\\screenCapture\\"
const val configPath = "config.properties"

lateinit var mConfig: ConfigLoader.Config
const val KEY_PATH: String = "PATH"
const val KEY_HIDE_EXT: String = "HIDE_EXT"
val KEYS: Array<String> = arrayOf(KEY_PATH, KEY_HIDE_EXT)

const val CMD_HIDE_EXT: Int = 0
const val CMD_SHOW_EXT: Int = 1

fun main(args: Array<String>) {
    loadConfig(configPath)
    if(args.isNotEmpty()){
        when (args[0].toInt()) {
            CMD_HIDE_EXT -> {
                renameAllToPNG();
            }
            CMD_SHOW_EXT -> {
                removePNGFromFile()
            }
            else -> {
                capScreen(getFolderPath())
            }
        }
    } else {
        capScreen(getFolderPath())
    }

}

fun listAllImageFile(consumer: (file: File) -> Unit) {
    val folder = File(mConfig.getProp(KEY_PATH))
    val subFolders: Array<File>? = folder.listFiles()
    if (subFolders != null) {
        for (subFolder in subFolders) {
            if (subFolder.isDirectory) {
                val files: Array<File>? = subFolder.listFiles()
                if (files != null) {
                    for (file in files) {
                        consumer(file)
                        println("File: " + file.absolutePath)
                    }
                }
            }
        }
    }
}

fun removePNGFromFile() {
    val ext = ".png"
    println("removePNGFromFile")
    listAllImageFile { file ->
            file.renameTo(File(file.parentFile.absolutePath + "\\" + file.name.replace(ext, "")))
    }
}

fun renameAllToPNG() {
    val ext = ".png"
    println("renameAllToPNG")
    listAllImageFile { file ->
        if(!file.name.endsWith(ext)) {
            file.renameTo(File(file.parentFile.absolutePath + "\\" + file.name + ext))
        }
    }
}

fun getFolderPath(): String {
    if (mConfig.getProp(KEY_PATH).isBlank()) {
        mConfig.setProp(KEY_PATH, PATH).save()
    }
    return mConfig.getProp(KEY_PATH)
}

fun loadConfig(configPath: String) {
    if (!File(configPath).exists()) {
        File(configPath).createNewFile()
        ConfigLoader.initPropFile(KEYS, configPath)
    }
    mConfig = ConfigLoader[configPath]
}

fun capScreen(path: String) {
    val image = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
    val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val folder = File(path + date)
    folder.mkdir()
    if (image != null) {
        ImageIO.write(image, "png", File(getScreenShotPath(folder)))
    }
}

fun getScreenShotPath(folder: File): String {
    val mtimestamp = SimpleDateFormat("yyyy-MM-dd-HHmmss").format(Date())
    val isHideExt: Boolean = !mConfig.getProp(KEY_HIDE_EXT).isBlank() || mConfig.getProp(KEY_HIDE_EXT).toBoolean()
    return if (isHideExt) folder.absolutePath + "\\" + "screenshot-" + mtimestamp else folder.absolutePath + "\\" + "screenshot-" + mtimestamp + ".png"
}




