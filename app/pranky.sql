-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 24, 2015 at 09:27 AM
-- Server version: 5.5.44-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pranky`
--

-- --------------------------------------------------------

--
-- Table structure for table `auth_device`
--

CREATE TABLE IF NOT EXISTS `auth_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model` varchar(255) NOT NULL,
  `gcm_id` varchar(255) NOT NULL,
  `app_id` varchar(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=20 ;

--
-- Dumping data for table `auth_device`
--

INSERT INTO `auth_device` (`id`, `model`, `gcm_id`, `app_id`) VALUES
(11, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'k3Yw'),
(12, 'LG-F240L', 'dtSxvGS99zI:APA91bE9VRXITYfR5EWDLRK7Z-Bc8AfF6CRrlUQhEhui4TB2dih0ssRzInS0noVeJ0UX2Q0-GTgZiduTvx-FzSbVSK4L49riUUNZAu4k_A9UrlFyu5iSah65jITObXmH4gyUhsJoCxkx', 'tgf7'),
(13, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'Ykh2'),
(14, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'yrr6'),
(15, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'Qepw'),
(16, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'O62h'),
(17, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'PPeW'),
(18, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'M6nn'),
(19, 'LG-D800', 'cO-toWqV9yE:APA91bHxDwpvI70Y3__4M1bDv987uPMFuPq0b7ZTcYWCTM34tRhtbmgNsmhvLAkosayEHOwbo9OPG3dUlkmxI_EUtsOFP2BDPDElUJ8BsavGJLocZyYS8A_gvORwHXLHnW4KJBziir76', 'JRnW');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
