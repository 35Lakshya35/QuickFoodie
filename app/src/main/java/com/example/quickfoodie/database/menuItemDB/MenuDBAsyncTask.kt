package com.example.quickfoodie.database.menuItemDB

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class MenuDBAsyncTask(var context: Context, var menuEntity: MenuEntity?, var mode: Int,var menuList:List<MenuEntity>?) :
    AsyncTask<Void, Void, ArrayList<MenuEntity>>() {

    /*Mode 1 :-add entity to database
      Mode 2 :-delete entity from database
      mode 3 :-get entity by id
      mode 4 :-get all entities as List
      mode 5 :-clearing database
     */

    private val db=Room.databaseBuilder(context,MenuDatabase::class.java,"MenuData").build()
    override fun doInBackground(vararg p0: Void?): ArrayList<MenuEntity> {
        when(mode){
            1->{
                db.menuDao().insert(menuEntity as MenuEntity)
                db.close()
            }
            2->{
                db.menuDao().delete(menuEntity as MenuEntity)
                db.close()
            }
            3->{
                val entity=db.menuDao().getDataById(menuEntity?.menu_id.toString())
                db.close()
                if(entity!= null){
                    return arrayListOf(entity) //List having only one menuEntity
                }
            }
            4->{
                val entityList=db.menuDao().getAllData()
                db.close()
                return entityList as ArrayList<MenuEntity>  //List with all entity
            }
            5->{
                db.menuDao().deleteAll(menuList as List<MenuEntity>)
                db.close()
            }
        }
        return arrayListOf()  //Empty List
    }
}