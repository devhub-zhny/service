package zhny.devhub.device.utils;

import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.entity.data.Node;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.data.Switch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Hardware {
     String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    public List<Gateway> parseSensorData(String sensorData) {
        sensorData = "A1AA0110FE01010001C5A7D3D1BB43ACD1A95D913D77C1FE010201013B807C971BCB1071232EAFE9AF8FFE02010100116800A5343FA1F21D1451197C51FE02020000F7A9C9545857F614B31327886C40FE03010100E9F8B7094A601052A8A463C3E743FE030201011F54A3F51676624339EDAE8B8ACBFE04010000EB68809A00C63B08C285FD5EFBACFE04020101808B3A82A263F104828A91147FECFE050101000F519CEA49AADA1B4DF0AEE9F780FE05020001F2D96835C8B694C548005B8FDEACFE06010001DE8B63DA6D555B0ABA2AC9171352FE06020001341375902A342EFE40FED533A752FE07010001FB7B2CD70D403BA8E83A171A5898FE07020100D3CE71496AAD113265234A72992CFE080101007E7CF4398027E7C9BEE3F55588EFFE08020001551450E823D5ED4B967B021D6B3CFE09010000FCF3ED03DBB982D4A4A5C4469EF6FE09020100F21C9B6AFE4B717CB68321B0CE70FE0A010001F40B5974FB9373A7A61B2BAA891AFE0A02000190EDF5705919B6AF116E76B92E6CFE0B0101000855D7BE07522C2624EE34B0CCF3FE0B0201018B66050532DEB3A30A3B8D7B2795FE0C010100E8F1E25A26D9362040BAC14BBF00FE0C020100AF9A90E9AD19B52AD6F5D73768B4FE0D0100016932E5517D89FA52617C85BFC222FE0D020101E2E3BF24F369990716D7B148F913FE0E0100012A8D828D094E40F12AC52A8BC930FE0E020100F6B40F48ED2CB02960AE1C508B65FE0F01000091CF5EF98DC9273EA1227CD120BBFE0F020001AC4C5170E9FD0F41FBD46188E40BFE10010100ACE2E6243F86267ECF640172C45EFE10020100EDFF8E579E5CBB1B72DEDE3D01D6FE11010101337293830B8367E1922178AA9D55FE11020001762C66AC2D5D5822FF5D23127479FE1201010113326D14979D7639C5A49E6A598FFE1202010099516F064787D71D0B51A51D2CC2FE1301000006673E5C7115B31CABA2F773772EFE13020101CB91B06AFCC14B49A45595F6E1AEFE140101011EEBFECFE235EDBAFB30B44AF8E4FE1402010057E9FF8D7177AD858B5CC15BF775FE15010000E5BF07A2C24E255383EA707924D5FE150200000B57095EBF4D511C8B335028B882FE1601000063D915075705DAAA1521E5C7B7B7FE1602000081DDDB929CC9694D25DC18FD030CFE17010100513B706E488955D959602FC30C5BFE170200008E5CB9D32C625806B162E22F735EFE180100017E05201C90C071DF2A9B55A9D7BEFE1802010122D8308F5C8E1FF319F9EACA28B1FE1901000019993EC2295781FF73C53AE9D45FFE190201009851CD1DE44BC012D9514E42C429FE1A010101E84CE501581CDF3DE6224C3405ECFE1A0201017330B12A35FBF472A70F48EEEB58FE1B010000ECC812A921A55AD9125D5BA882AA";
        List<Gateway> gatewayList = new ArrayList<>();

        // 获取包头，基地地址和网关地址
        String header = sensorData.substring(0, 4);
        String baseAddress = sensorData.substring(4, 6);
        String gatewayAddress = sensorData.substring(6, 8);

        // 解析传感器数据
        int sensorDataLength = 19 * 2; // 每个传感器数据的长度

        // 使用 Map 存储每个节点的传感器列表
        Map<Long, List<Sensor>> nodeSensorMap = new HashMap<>();

        for (int i = 8; i < sensorData.length(); i += sensorDataLength) {
            // 计算节点ID
            long nodeId = Long.parseLong(baseAddress + gatewayAddress + sensorData.substring(i + 2, i + 4), 16);

            // 检查节点是否已存在，如果不存在则创建新节点，否则从 Map 中获取现有节点
            List<Sensor> sensorList = nodeSensorMap.computeIfAbsent(nodeId, k -> new ArrayList<>());

            // 定义传感器类型数组
            String[] sensorTypes = {"temperature", "humidity", "conductivity", "ph", "nitrogen", "phosphorus", "potassium"};
            String[] units = {"Celsius", "%", "µS/cm", "", "mg/L", "mg/L", "mg/L"};

            for (int j = 0; j < sensorTypes.length; j++) {
                Sensor sensor = new Sensor();
                sensor.setParentDeviceId(nodeId);
                // 计算传感器 ID，采用相同的逻辑
                String sensorIdHex = baseAddress + gatewayAddress + sensorData.substring(i + 2, i + 6) + (j + 1);
                sensor.setSensorId(Long.parseLong(sensorIdHex, 16));//sensorData.substring(i + 10, i + 12))
//                sensor.setDeviceStatus("online");//sensorData.substring(i + 8, i + 10))
//                sensor.setIsOpen(true);//sensorData.substring(i + 6, i + 8)
                if (sensorData.substring(i + 6, i + 8).equals("01")){
                    sensor.setIsOpen(true);
                }
                if (sensorData.substring(i + 6, i + 8).equals("00")){
                    sensor.setIsOpen(false);
                }
                if (sensorData.substring(i + 8, i + 10).equals("01")){
                    sensor.setDeviceStatus("online");
                }
                if (sensorData.substring(i + 8, i + 10).equals("00")){
                    sensor.setDeviceStatus("offline");
                }
                sensor.setTimestamp(time);
                sensor.setSensorType(sensorTypes[j]);

                // 获取对应的数据偏移量
                int dataOffset = 10 + j * 4;

                // 获取十六进制数据并转换为 double 类型
                String hexData = sensorData.substring(i + dataOffset, i + dataOffset + 4);
                double value = Double.longBitsToDouble(Long.parseLong(hexData, 16));
                sensor.setValue(value);

                // 设置单位
                sensor.setUnit(units[j]);

                sensorList.add(sensor);
            }
        }

        // 创建网关
        Gateway gateway = new Gateway();
        gateway.setGatewayId(Long.parseLong(sensorData.substring(4, 8)));
        gateway.setDeviceStatus("online");
        gateway.setIsOpen(true);
        gateway.setTimestamp(time);

        // 创建节点列表并添加到网关中
        List<Node> nodeList = new ArrayList<>();
        for (Map.Entry<Long, List<Sensor>> entry : nodeSensorMap.entrySet()) {
            Node node = new Node();
            node.setNodeId(entry.getKey());
            node.setDeviceStatus("online");
            node.setIsOpen(true);
            node.setTimestamp(time);
            node.setParentDeviceId(Long.parseLong(sensorData.substring(4, 8)));
            node.setSensors(entry.getValue());
            node.setSwitches(Collections.emptyList());
            nodeList.add(node);
        }
        gateway.setNodes(nodeList);

        // 将网关添加到网关列表中
        gatewayList.add(gateway);

        return gatewayList;
    }

    public List<Gateway> parseSwitchData(String switchData) {
        switchData = "A2AA0110FE01010001FE01020001FE02010000FE02020000FE03010101FE03020000FE04010000FE04020001FE05010001FE05020101FE06010001FE06020100FE07010000FE07020001FE08010000FE08020001FE09010001FE09020001FE0A010101FE0A020000FE0B010100FE0B020100FE0C010000FE0C020001FE0D010101FE0D020000FE0E010100FE0E020100FE0F010001FE0F020101FE10010000FE10020001FE11010101FE11020000FE12010000FE12020000FE13010100FE13020001FE14010000FE14020100FE15010000FE15020001FE16010000FE16020100FE17010100FE17020001FE18010100FE18020000FE19010001FE19020000FE1A010000FE1A020101FE1B010000FE1B020001FE1C010000FE1C020001FE1D010100FE1D020001FE1E010001FE1E020100FE1F010101FE1F020001FE20010101FE20020100FE21010100FE21020101FE22010100FE22020100FE23010001FE23020000FE24010100FE24020100FE25010100FE25020100FE26010001FE26020101FE27010000FE27020001FE28010101FE28020101FE29010100FE29020000FE2A010000FE2A020000FE2B010001FE2B020000FE2C010100FE2C020100FE2D010100FE2D020101FE2E010100FE2E020100FE2F010101FE2F020100FE30010101FE30020101FE31010100FE31020001FE32010101FE32020101FE33010100FE33020000FE34010101FE34020100FE35010100FE35020101FE36010000FE36020101FE37010100FE37020101FE38010100FE38020000FE39010100FE39020000FE3A010001FE3A020100FE3B010100FE3B020000FE3C010100FE3C020101FE3D010000FE3D020100FE3E010101FE3E020000FE3F010000FE3F020000FE40010001FE40020100FE41010100FE41020100FE42010100FE42020101FE43010101FE43020000FE44010000FE44020101FE45010100FE45020001FE46010001FE46020000FE47010001FE47020100FE48010100FE48020100FE49010101FE49020001FE4A010100FE4A020101FE4B010000FE4B020100FE4C010101FE4C020000FE4D010000FE4D020001FE4E010101FE4E020001FE4F010001FE4F020001FE50010001FE50020001FE51010101FE51020101FE52010001FE52020101FE53010100FE53020000FE54010100FE54020000FE55010100FE55020001FE56010101FE56020101FE57010000FE57020101FE58010101FE58020100FE59010101FE59020100FE5A010000FE5A020101FE5B010101FE5B020100FE5C010001FE5C020000FE5D010001FE5D020101FE5E010001FE5E020001FE5F010000FE5F020100FE60010100FE60020100FE61010100FE61020000FE62010100FE62020001FE63010100FE63020001FE64010100FE64020100";
        List<Gateway> gatewayList = new ArrayList<>();

        // 获取包头，基地地址和网关地址
        String header = switchData.substring(0, 4);
        String baseAddress = switchData.substring(4, 6);
        String gatewayAddress = switchData.substring(6, 8);

        // 解析开关数据
        int switchDataLength = 5 * 2; // 每个开关数据的长度

        // 使用 Map 存储每个节点的开关列表
        Map<Long, List<Switch>> nodeSwitchMap = new HashMap<>();

        for (int i = 8; i < switchData.length(); i += switchDataLength) {
            // 计算节点ID
            long nodeId = Long.parseLong(baseAddress + gatewayAddress + switchData.substring(i + 2, i + 4), 16);

            // 检查节点是否已存在，如果不存在则创建新节点，否则从 Map 中获取现有节点
            List<Switch> switchList = nodeSwitchMap.computeIfAbsent(nodeId, k -> new ArrayList<>());

            // 创建开关对象并添加到节点的开关列表中
            Switch s = new Switch();
            if (switchData.substring(i + 6, i + 8).equals("01")){
                s.setIsOpen(true);
            }
            if (switchData.substring(i + 6, i + 8).equals("00")){
                s.setIsOpen(false);
            }
            if (switchData.substring(i + 8, i + 10).equals("01")){
                s.setDeviceStatus("online");
            }
            if (switchData.substring(i + 8, i + 10).equals("00")){
                s.setDeviceStatus("offline");
            }
            s.setTimestamp(time);
            s.setSwitchId(Long.parseLong(baseAddress + gatewayAddress + switchData.substring(i + 2, i + 6), 16));
            s.setParentDeviceId(nodeId);
            switchList.add(s);
        }

        // 创建网关
        Gateway gateway = new Gateway();
        gateway.setGatewayId(Long.parseLong(switchData.substring(4, 8)));
        gateway.setDeviceStatus("online");
        gateway.setIsOpen(true);
        gateway.setTimestamp(time);

        // 创建节点列表并添加到网关中
        List<Node> nodeList = new ArrayList<>();
        for (Map.Entry<Long, List<Switch>> entry : nodeSwitchMap.entrySet()) {
            Node node = new Node();
            node.setNodeId(entry.getKey());
            node.setDeviceStatus("online");
            node.setIsOpen(true);
            node.setTimestamp(time);
            node.setParentDeviceId(Long.parseLong(switchData.substring(4, 8)));
            node.setSwitches(entry.getValue());
            node.setSensors(Collections.emptyList());
            nodeList.add(node);
        }
        gateway.setNodes(nodeList);

        // 将网关添加到网关列表中
        gatewayList.add(gateway);

        return gatewayList;
    }






}
