package zzh.lifeplayer.music.fragments.lyric


import java.io.File

/**
 * LyricsManager handles fetching and displaying synchronized lyrics (LRC format).
 */
class LyricsManager {

    /**
     * Fetches synchronized lyrics from a local LRC file.
     * @param title The title of the song.
     * @param artist The artist of the song.
     * @return The content of the LRC file as a string, or null if not found.
     */
    fun fetchLyricsFromLocal(title: String, artist: String): String? {
        val sanitizedTitle = title.replace(" ", "_").lowercase()
        val sanitizedArtist = artist.replace(" ", "_").lowercase()
        val lrcFileName = "$sanitizedArtist-$sanitizedTitle.lrc"

        val lrcFile = File("lyrics/$lrcFileName")
        return if (lrcFile.exists()) {
            lrcFile.readText()
        } else {
            null
        }
    }

    /**
     * Parses the content of an LRC file into a map of timestamps and lyrics.
     * @param lrcContent The content of the LRC file.
     * @return A map where the key is the timestamp and the value is the lyric line.
     */
    fun parseLrcContent(lrcContent: String): Map<String, String> {
        val lyricsMap = mutableMapOf<String, String>()
        val lines = lrcContent.lines()

        for (line in lines) {
            val matchResult = Regex("\\[(\\d{2}:\\d{2}\\.\\d{2})\\](.*)").find(line)
            if (matchResult != null) {
                val (timestamp, lyric) = matchResult.destructured
                lyricsMap[timestamp] = lyric
            }
        }

        return lyricsMap
    }
}