package com.xielbs.database;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Config {

	private static Map<String, Config> file_readwriters = new LinkedHashMap<String, Config>();
	private File loadFile;
	private Map<String, Map<String, Element>> key_items = new LinkedHashMap<String, Map<String, Element>>();
	private Document documnet;
	private Element root;
	private boolean isLoad = false;

	private Config(File file) {
		loadFile = file;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static Config getInstance(File file) {
		Config crw = file_readwriters.get(file.getPath());
		if (crw == null) {
			crw = new Config(file);
			file_readwriters.put(file.getPath(), crw);
		}
		return crw;
	}

	/**
	 * 
	 * @throws ConfigException
	 */
	public void load() throws ConfigException {
		if (isLoad)
			return;
		if (loadFile == null || !loadFile.exists()) {
			throw new ConfigException(loadFile.getPath() + "文件不存在");
		}
		SAXBuilder builder = new SAXBuilder();
		try {
			documnet = builder.build(loadFile);
			root = documnet.getRootElement();
			if (root == null) {
				throw new JDOMException();
			}
			List<?> list = root.getChildren("config");
			if (list != null) {
				for (Object obj : list) {
					Element config = (Element) obj;
					String name = config.getAttributeValue("name");
					if (name == null || name.trim().equals("")) {
						continue;
					}
					List<?> sublist = config.getChildren("item");
					if (sublist != null) {
						Map<String, Element> subMap = new LinkedHashMap<String, Element>();
						for (Object subObj : sublist) {
							String key = ((Element) subObj).getAttributeValue("key");
							if (key == null || key.trim().equals("")) {
								continue;
							}
							subMap.put(key.trim(),(Element) subObj);
						}
						key_items.put(name.trim(), subMap);
					}
				}
				isLoad = true;
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new ConfigException(loadFile.getPath() + "文件不存在" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConfigException(e.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean write() {
		try {
			load();
		} catch (ConfigException e1) {
			e1.printStackTrace();
			return false;
		}
		Format format = Format.getCompactFormat();
		format.setEncoding("UTF-8"); 
		format.setIndent("    "); 
		XMLOutputter outputter = new XMLOutputter(format);
		try {
			outputter.output(documnet, new FileOutputStream(loadFile));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		String[] keys = key.toLowerCase().split("/");
		if (keys == null || keys.length != 2)
			return null;
		try {
			load();
		} catch (ConfigException e) {
			e.printStackTrace();
			return null;
		}
		Map<String, Element> map = key_items.get(keys[0]);
		if (map != null) {
			Element item = map.get(keys[1]);
			if (item != null)
				return item.getAttributeValue("value");
		}
		return null;
	}

	public boolean hasKey(String key) {
		boolean has = false;
		if (key_items.containsKey(key))
			has = true;
		return has;
	}

	/**
	 * 
	 * @param configKey
	 * @param itemKey
	 * @return
	 */
	public String getValue(String configKey, String itemKey) {
		try {
			load();
		} catch (ConfigException e) {
			e.printStackTrace();
			return null;
		}
		Map<String, Element> map = key_items.get(configKey);
		if (map != null) {
			Element item = map.get(itemKey);
			if (item != null)
				return item.getAttributeValue("value");
		}
		return null;
	}

	/**
	 * 
	 * @param configKey
	 * @param itemKey
	 * @param defaulValue
	 * @return
	 */
	public String getValue(String configKey, String itemKey, String defaulValue) {
		String valueString = getValue(configKey, itemKey);
		if (null == valueString) {
			return defaulValue;
		}
		return valueString;
	}

	public void setValue(String key, String value) {
		String[] keys = key.toLowerCase().split("/");
		if (keys == null || keys.length != 2)
			return;
		try {
			load();
		} catch (ConfigException e) {
			e.printStackTrace();
			return;
		}
		Map<String, Element> map = key_items.get(keys[0]);
		if (map != null) {
			Element item = map.get(keys[1]);
			if (item != null) {
				item.setAttribute("value", value);
			}
		}
	}

	/**
	 * 
	 */
	public void setValue(String configKey, String itemKey, String value) {
		try {
			load();
		} catch (ConfigException e) {
			e.printStackTrace();
			return;
		}
		Map<String, Element> map = key_items.get(configKey.toLowerCase());
		if (map != null) {
			Element item = map.get(itemKey.toLowerCase());
			if (item != null) {
				item.setAttribute("value", value);
			}
		}
	}

	public void setValue(String key, int value) {
		setValue(key, String.valueOf(value));
	}

	public void setValue(String configKey, String itemKey, int value) {
		setValue(configKey, itemKey, String.valueOf(value));
	}

	public Boolean getBoolean(String configKey, String itemKey)
			throws ConfigException {
		String valueString = getValue(configKey, itemKey);
		try {
			if (null == valueString) {
				throw new ConfigException(MSG_NULLVALUE);
			}
			return Boolean.valueOf(valueString);
		} catch (Exception e) {
			throw new ConfigException(configKey, itemKey, valueString, e);
		}
	}

	public Boolean getBoolean(String configKey, String itemKey,
			boolean defauleValue) {
		try {
			String valueString = getValue(configKey, itemKey);
			if (null == valueString) {
				return defauleValue;
			}
			return Boolean.valueOf(valueString);
		} catch (Exception e) {
			return defauleValue;
		}
	}

	public void setBoolean(String configKey, String itemKey, Boolean value) {
		setValue(configKey, itemKey, value.toString());
	}

	public Integer getInteger(String configKey, String itemKey)
			throws ConfigException {
		Long value = getLong(configKey, itemKey);
		if (value == null)
			return null;
		return value.intValue();
	}

	public Integer getInteger(String configKey, String itemKey,
			Integer defauleValue) {
		return getLong(configKey, itemKey, defauleValue.longValue()).intValue();
	}

	public void setInteger(String configKey, String itemKey, Integer value) {
		setValue(configKey, itemKey, value.toString());
	}

	public Long getLong(String configKey, String itemKey)
			throws ConfigException {
		String valueString = getValue(configKey, itemKey);
		if (valueString == null)
			return null;
		try {
			return Long.valueOf(valueString);
		} catch (NumberFormatException e) {
			throw new ConfigException(configKey, itemKey, valueString,
					ERROR_PARSE_NUMER, e);
		}
	}

	public Long getLong(String configKey, String itemKey, Long defauleValue) {
		String valueString = getValue(configKey, itemKey);
		try {
			return Long.valueOf(valueString);
		} catch (NumberFormatException e) {
			return defauleValue;
		}
	}

	public void setLong(String configKey, String itemKey, Long value) {
		setValue(configKey, itemKey, value.toString());
	}

	private static final String MSG_NULLVALUE = "不能为空";
	private static final String ERROR_PARSE_NUMER = "数字解析错误";

//	public static void main(String[] args) {
//		Config cfg = Config.getInstance(new File(
//				"config/NeuSoftSG186_DataSource.xml"));
//		System.out.println(cfg.getValue("WebService", "connection_url"));
//		System.out.println(cfg.getValue("WebService", "user_name"));
//	}

	/**
	 * @param name
	 * @return
	 */
	public Object[] getKeys(String name) {
		Map<String, Element> map = key_items.get(name);
		if (map != null && map.keySet() != null)
			return map.keySet().toArray();
		return null;
	}


}
