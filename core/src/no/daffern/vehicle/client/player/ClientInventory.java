package no.daffern.vehicle.client.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.client.handlers.CameraHandler;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.graphics.NinePatchRepeated;
import no.daffern.vehicle.graphics.TextField;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.network.packets.InventoryPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Daffern on 25.03.2017.
 */
public class ClientInventory implements SystemInterface {



    int inventoryWidth = 10, inventoryHeight = 1;
    Map<Integer, InventorySlot> inventory = new ConcurrentHashMap<Integer, InventorySlot>();
    int selectedItem = 0;

    Texture selectedTexture;
    NinePatchRepeated ninePatchRepeated;

    Vector2 actionbarPos = new Vector2();
    Vector2 itemPosition = new Vector2();

    float scaleX = 2, scaleY = 2;

    ClientPlayer currentPlayer;


    public ClientInventory() {

        ResourceManager.loadAsset("images/interface/uibox.png", Texture.class, new ResourceManager.AssetListener<Texture>() {
            @Override
            public void onAssetLoaded(Texture asset) {
                ninePatchRepeated = new NinePatchRepeated(asset, 7, 7, 7, 7);
                ninePatchRepeated.setStretch(scaleX, scaleY);
                ninePatchRepeated.setRepeats(inventoryWidth, inventoryHeight);

                updatePosition(C.cameraHandler.uiCamera);
            }
        });
        ResourceManager.loadAsset("images/interface/selected.png", Texture.class, new ResourceManager.AssetListener<Texture>() {
            @Override
            public void onAssetLoaded(Texture asset) {
                selectedTexture = asset;
            }
        });

        C.cameraHandler.listen(new CameraHandler.CameraListener() {
            @Override
            public void sizeUpdated(OrthographicCamera gameCamera, OrthographicCamera uiCamera, OrthographicCamera debugCamera) {
                updatePosition(uiCamera);

            }
        });


        C.myClient.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof InventoryPacket) {

                    InventoryPacket inventoryPacket = (InventoryPacket) object;

                    if (inventoryPacket.playerId == currentPlayer.playerId) {
                        handleInventoryPacket(inventoryPacket);
                    }
                }

            }
        });
    }

    private void handleInventoryPacket(InventoryPacket inventoryPacket) {

        //remove slot if empty
        if (inventoryPacket.count <= 0){
            inventory.remove(inventoryPacket.itemId);
        }else {

            InventorySlot inventorySlot = inventory.get(inventoryPacket.itemId);

            if (inventorySlot == null) {
                inventorySlot = new InventorySlot(inventoryPacket);
                inventory.put(inventoryPacket.itemId, inventorySlot);
            } else {
                inventorySlot.setCount(inventoryPacket.count);
            }

        }
    }

    public boolean keyDown(int keycode) {
        if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_6) {
            selectedItem = keycode - Input.Keys.NUM_1;
            return true;
        }
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 mousePos = Tools.mouseToWorldCoordinates(C.cameraHandler.uiCamera, screenX, screenY);

        Vector2 transPos = new Vector2(mousePos.x - itemPosition.x, mousePos.y - itemPosition.y);

        Vector2 size = ninePatchRepeated.getSize();

        if (transPos.x > 0 && transPos.x < size.x) {
            if (transPos.y > 0 && transPos.y < size.y) {

                int selected = (int) (transPos.x / ninePatchRepeated.getMiddleWidth());

                selected = Math.min(selected, getInventorySize() - 1);
                selectedItem = Math.max(0, selected);
                return true;
            }
        }

        return false;
    }

    public InventorySlot getSelected() {

        int i = 0;
        for (Map.Entry<Integer, InventorySlot> entry : inventory.entrySet()){
            if (i == selectedItem)
                return entry.getValue();
            i++;
        }

        return null;
    }

    public int getInventorySize() {
        return inventoryHeight * inventoryWidth;
    }

    public void setCurrentPlayer(ClientPlayer clientPlayer) {
        this.currentPlayer = clientPlayer;
    }


    private void updatePosition(OrthographicCamera camera) {
        Vector2 size = ninePatchRepeated.getSize();

        actionbarPos.x = -size.x / 2;
        actionbarPos.y = -camera.viewportHeight / 2;

        itemPosition.x = actionbarPos.x + 9 * scaleX;
        itemPosition.y = actionbarPos.y + 9 * scaleY;
    }

    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {

    }

    @Override
    public void render(Batch batch, float delta) {

        //background
        if (ninePatchRepeated != null)
            ninePatchRepeated.draw(batch, actionbarPos.x, actionbarPos.y);

        //items
        int x = 0, y = 0;
        for (Map.Entry<Integer, InventorySlot> entry : inventory.entrySet()) {
            if (x == inventoryWidth) {
                y++;
                x = 0;
            }
            entry.getValue().render(batch, itemPosition.x + x * 33, itemPosition.y + y * 33, 24, 24);
            x++;
        }


        //selected
        if (selectedTexture != null) {
            batch.draw(selectedTexture,
                    actionbarPos.x + selectedItem % inventoryWidth * ninePatchRepeated.getMiddleWidth(), actionbarPos.y + selectedItem / inventoryWidth * ninePatchRepeated.getMiddleHeight(),
                    selectedTexture.getWidth() * scaleX, selectedTexture.getHeight() * scaleY);
        }
    }


    public class InventorySlot {
        int itemId;
        private int count;
        TextureAtlas.AtlasRegion textureRegion;
        TextField textField;

        public InventorySlot(InventoryPacket inventoryPacket) {
            this.itemId = inventoryPacket.itemId;
            this.count = inventoryPacket.count;

            textField = new TextField(""+count);

            C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener() {
                @Override
                public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
                    textureRegion = textureAtlas.findRegion(gameItem.iconName);
                }
            });

        }
        public void setCount(int count){
            this.count = count;
            if (textField != null)
                textField.setText(count+"");
        }
        public int getItemId() {
            return itemId;
        }
        public int getCount(){
            return count;
        }

        public void render(Batch batch, float x, float y, float width, float height) {
            if (textureRegion != null)
                batch.draw(textureRegion, x, y, width, height);
            if (textField != null)
                textField.render(batch,x + width-7,y+10);
        }
    }

}
