
package com.netsuite.webservices.platform.messages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AsyncUpsertListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AsyncUpsertListResult">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:messages_2014_2.platform.webservices.netsuite.com}AsyncResult">
 *       &lt;sequence>
 *         &lt;element ref="{urn:messages_2014_2.platform.webservices.netsuite.com}writeResponseList"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AsyncUpsertListResult", propOrder = {
    "writeResponseList"
})
public class AsyncUpsertListResult
    extends AsyncResult
{

    @XmlElement(required = true)
    protected WriteResponseList writeResponseList;

    /**
     * Gets the value of the writeResponseList property.
     * 
     * @return
     *     possible object is
     *     {@link WriteResponseList }
     *     
     */
    public WriteResponseList getWriteResponseList() {
        return writeResponseList;
    }

    /**
     * Sets the value of the writeResponseList property.
     * 
     * @param value
     *     allowed object is
     *     {@link WriteResponseList }
     *     
     */
    public void setWriteResponseList(WriteResponseList value) {
        this.writeResponseList = value;
    }

}