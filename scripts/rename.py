import os
import shutil

# === 配置 ===
OLD_PACKAGE = "code.name.monkey.retromusic"
NEW_PACKAGE = "zzh.lifeplayer.music"
FILE_EXTENSIONS = (".java", ".kt", ".xml", ".gradle", ".manifest")

def package_to_path(package_name):
    return package_name.replace(".", "/")

def update_file_content(filepath, old_pkg, new_pkg):
    if not os.path.isfile(filepath):
        return
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        if old_pkg not in content:
            return
        new_content = content.replace(old_pkg, new_pkg)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"[✓] 内容替换: {filepath}")
    except Exception as e:
        print(f"[!] 替换失败: {filepath} 错误: {e}")

def safe_move_dir(src, dst):
    if not os.path.exists(src):
        print(f"[!] 找不到目录: {src}")
        return
    if os.path.exists(dst):
        print(f"[!] 目标目录已存在，跳过: {dst}")
        return
    try:
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.move(src, dst)
        print(f"[→] 包目录迁移: {src} → {dst}")
    except Exception as e:
        print(f"[!] 目录移动失败: {e}")

def clean_empty_dirs(root_dir):
    for dirpath, dirnames, filenames in os.walk(root_dir, topdown=False):
        if not dirnames and not filenames:
            try:
                os.rmdir(dirpath)
                print(f"[✘] 删除空目录: {dirpath}")
            except Exception:
                pass

def update_package_in_module(module_path):
    src_path = os.path.join(module_path, "src")
    if not os.path.isdir(src_path):
        return

    for variant in os.listdir(src_path):
        variant_path = os.path.join(src_path, variant)
        java_base = os.path.join(variant_path, "java")
        if not os.path.isdir(java_base):
            continue

        old_pkg_path = os.path.join(java_base, package_to_path(OLD_PACKAGE))
        new_pkg_path = os.path.join(java_base, package_to_path(NEW_PACKAGE))

        if os.path.isdir(old_pkg_path):
            # 替换内容
            for dirpath, _, filenames in os.walk(variant_path):
                for filename in filenames:
                    if filename.endswith(FILE_EXTENSIONS):
                        update_file_content(os.path.join(dirpath, filename), OLD_PACKAGE, NEW_PACKAGE)

            # 移动目录
            safe_move_dir(old_pkg_path, new_pkg_path)
            clean_empty_dirs(java_base)
        else:
            print(f"[!] 跳过未找到包目录: {old_pkg_path}")

def update_all_modules(root_dir):
    for name in os.listdir(root_dir):
        module_path = os.path.join(root_dir, name)
        build_file = os.path.join(module_path, "build.gradle")
        if os.path.isdir(module_path) and os.path.exists(build_file):
            print(f"\n📦 模块识别: {name}")
            update_package_in_module(module_path)

def update_root_files(root_dir):
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith(FILE_EXTENSIONS):
                update_file_content(os.path.join(dirpath, filename), OLD_PACKAGE, NEW_PACKAGE)

def main():
    root = os.getcwd()
    print(f"\n=== 开始包名替换 ===")
    print(f"旧包名: {OLD_PACKAGE}")
    print(f"新包名: {NEW_PACKAGE}\n")

    update_all_modules(root)
    print("\n=== 替换根目录文件内容 ===")
    update_root_files(root)

    print("\n✅ 完成包名重构！")

if __name__ == "__main__":
    main()