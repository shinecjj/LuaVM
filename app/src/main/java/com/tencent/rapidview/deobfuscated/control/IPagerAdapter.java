/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at

 http://opensource.org/licenses/MIT

 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/
package com.tencent.rapidview.deobfuscated.control;


import com.tencent.rapidview.deobfuscated.IRapidView;

import com.tencent.jse.vm2.LuaTable;

/**
 * @Class IPagerAdapter
 * @Desc pageradapter不混淆接口
 *
 * @author arlozhang
 * @date 2017.07.07
 */
public interface IPagerAdapter {

    void addView(IRapidView view);

    void refresh(LuaTable tableView);

    IRapidView getChildView(String name);
}
