#for file in *.wav; do
#  ffmpeg -i "$file" "${file%.wav}.mp3"
#done

for file in *.wav; do
  ffmpeg -i "$file" -codec:a libmp3lame -q:a 0 "${file%.wav}.mp3"
done

