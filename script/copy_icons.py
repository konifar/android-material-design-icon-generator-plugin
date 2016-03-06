import os
import shutil
from os.path import join

icons_root_dir = "./material-design-icons"
output_root_dir = "./src/com/konifar/material_icon_generator/icons"
drawables = ["anydpi-v21", "hdpi", "mdpi", "xhdpi", "xxhdpi", "xxxhdpi"]

category_dirs = [f for f in os.listdir(icons_root_dir) if os.path.isdir(join(icons_root_dir, f))]
# Remove not android icons dir.
category_dirs.remove("iconfont")
category_dirs.remove("sprites")
category_dirs.remove("www")

for category in category_dirs:
    for drawable in drawables:
        icons_dir = icons_root_dir + "/" + category + "/drawable-" + drawable
        output_dir_path = output_root_dir + "/" + category + "/drawable-" + drawable

        if os.path.exists(output_dir_path):
          shutil.rmtree(output_dir_path)
        os.makedirs(output_dir_path)

        icons = [icon for icon in os.listdir(icons_dir + "/") if '_black_' in icon]
        for icon in icons:
          print output_dir_path + "/" + icon
          shutil.copyfile(icons_dir + "/" + icon, output_dir_path + "/" + icon)
