package zzh.lifeplayer.music.extensions

import java.io.BufferedOutputStream
import java.util.zip.ZipOutputStream

fun BufferedOutputStream.zipOutputStream(): ZipOutputStream = ZipOutputStream(this)