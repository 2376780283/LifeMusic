import os
from PIL import Image

# 设置源目录和目标目录
source_directory = './'  # 当前目录，可以根据需要修改
output_directory = './converted_pngs/'  # 输出目录，可以修改

# 如果目标目录不存在，创建它
if not os.path.exists(output_directory):
    os.makedirs(output_directory)

# 遍历目录下的所有文件
for filename in os.listdir(source_directory):
    # 检查文件是否是 jpg 格式
    if filename.lower().endswith('.jpg'):
        # 构造文件的完整路径
        file_path = os.path.join(source_directory, filename)
        
        # 打开图片并转换为 PNG 格式
        try:
            with Image.open(file_path) as img:
                # 去掉扩展名并添加 .png
                output_file = os.path.splitext(filename)[0] + '.png'
                output_path = os.path.join(output_directory, output_file)
                
                # 保存为 PNG 格式
                img.save(output_path, 'PNG')
                print(f'Converted: {filename} -> {output_file}')
        except Exception as e:
            print(f"Error processing {filename}: {e}")

print("Conversion completed.")