import os
from os.path import join

icons_root_dir = "./material-design-icons"
category_dirs = [f for f in os.listdir(icons_root_dir) if os.path.isdir(join(icons_root_dir, f))]
# Remove not android icons dir.
category_dirs.remove("iconfont")
category_dirs.remove("sprites")
category_dirs.remove("www")

template_file = open('./src/com/konifar/material_icon_generator/template.xml', 'w')
template_file.write('<?xml version="1.0"?>\n')
template_file.write('<icons default="action/ic_3d_rotation">\n\n')

for category in category_dirs:
    icons_dir = icons_root_dir + "/" + category + "/drawable-mdpi"
    icons = [icon for icon in os.listdir(icons_dir + "/") if '_white_18dp' in icon]
    for icon in icons:
        icon_name = category + "/" + icon.replace('_white_18dp.png', '')
        print icon_name
        template_file.write('    <option id="' + icon_name + '">' + icon_name + '</option>\n')

template_file.write('\n</icons>')
template_file.close()
