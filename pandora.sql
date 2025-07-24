-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 24, 2025 at 11:49 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pandora`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `email` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `level` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `saldo` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`email`, `name`, `level`, `password`, `saldo`) VALUES
('123', '123', 'user', '123', 240000),
('321', '321', 'admin', '321', 0),
('test', 'test', 'user', 'test', 250000);

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE `image` (
  `image_id` int(11) NOT NULL,
  `image_path` varchar(500) NOT NULL,
  `image_name` varchar(200) NOT NULL,
  `image_price` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `image`
--

INSERT INTO `image` (`image_id`, `image_path`, `image_name`, `image_price`) VALUES
(1, '/data/user/0/com.example.pandora/files/uploaded_1753346817434.jpg', 'monkey', '60000'),
(2, '/data/user/0/com.example.pandora/files/uploaded_1753348922721.jpg', 'harimau sigma', '100000'),
(3, '/data/user/0/com.example.pandora/files/uploaded_1753348972789.jpg', 'beruang hitam', '80000'),
(4, '/data/user/0/com.example.pandora/files/uploaded_1753348997323.jpg', 'beruang putih', '90000'),
(5, '/data/user/0/com.example.pandora/files/uploaded_1753349023774.jpg', 'harimau yapping', '50000');

-- --------------------------------------------------------

--
-- Table structure for table `purchases`
--

CREATE TABLE `purchases` (
  `purchase_id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `image_id` int(11) NOT NULL,
  `image_name` varchar(200) NOT NULL,
  `price` int(11) NOT NULL,
  `purchase_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `purchases`
--

INSERT INTO `purchases` (`purchase_id`, `email`, `image_id`, `image_name`, `price`, `purchase_date`) VALUES
(1, '123', 1, 'monkey', 60000, '2025-07-24 09:00:12'),
(2, 'test', 5, 'harimau yapping', 50000, '2025-07-24 09:28:51');

-- --------------------------------------------------------

--
-- Table structure for table `uploaded_images`
--

CREATE TABLE `uploaded_images` (
  `id` int(11) NOT NULL,
  `image_path` text DEFAULT NULL,
  `image_name` varchar(255) DEFAULT NULL,
  `image_price` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`image_id`);

--
-- Indexes for table `purchases`
--
ALTER TABLE `purchases`
  ADD PRIMARY KEY (`purchase_id`),
  ADD KEY `email` (`email`),
  ADD KEY `image_id` (`image_id`);

--
-- Indexes for table `uploaded_images`
--
ALTER TABLE `uploaded_images`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `image`
--
ALTER TABLE `image`
  MODIFY `image_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `purchases`
--
ALTER TABLE `purchases`
  MODIFY `purchase_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `uploaded_images`
--
ALTER TABLE `uploaded_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `purchases`
--
ALTER TABLE `purchases`
  ADD CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`email`) REFERENCES `account` (`email`),
  ADD CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`image_id`) REFERENCES `image` (`image_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
