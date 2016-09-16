import os
import shutil
import argparse
from os.path import join

p = argparse.ArgumentParser()
p.add_argument('-v', action='store_true', help="output log to console")

args = p.parse_args()
verbose = args.v

icons_root_dir = "./material-design-icons"
output_root_dir = "./src/main/resources/icons"
drawables = ["anydpi-v21", "hdpi", "mdpi", "xhdpi", "xxhdpi", "xxxhdpi"]

category_dirs = [f for f in os.listdir(icons_root_dir) if os.path.isdir(join(icons_root_dir, f))]
# Remove not android icons dir.
category_dirs.remove("iconfont")
category_dirs.remove("sprites")

for category in category_dirs:
    for drawable in drawables:
        icons_dir = icons_root_dir + "/" + category + "/drawable-" + drawable
        output_dir_path = output_root_dir + "/" + category + "/drawable-" + drawable

        if os.path.exists(output_dir_path):
          shutil.rmtree(output_dir_path)
        os.makedirs(output_dir_path)

        icons = [icon for icon in os.listdir(icons_dir + "/") if '_black_' in icon]
        for icon in icons:
          if verbose:
            print output_dir_path + "/" + icon
          shutil.copyfile(icons_dir + "/" + icon, output_dir_path + "/" + icon)
